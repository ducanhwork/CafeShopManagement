package com.group3.application.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.OrderDetailItemDTO;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.dto.OrderUpdateDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.AuthRepository;
import com.group3.application.model.repository.OrderRepository;
import com.group3.application.model.repository.TableRepository;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDetailViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final AuthRepository authRepository;
    public final MutableLiveData<Order> order = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<APIResult> updateResult = new MutableLiveData<>();

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        tableRepository = new TableRepository(application.getApplicationContext());
        authRepository = new AuthRepository(application);
    }

    public void fetchOrderDetails(String orderId) {
        isLoading.setValue(true);
        String token = authRepository.getAuthToken();
        if (token == null) {
            error.postValue("Lỗi xác thực, vui lòng đăng nhập lại");
            isLoading.postValue(false);
            return;
        }
        orderRepository.getOrderDetails(token, orderId, result -> {
            if (result.isSuccess() && result.getData() != null) {
                order.postValue(result.getData());
            } else {
                error.postValue(result.getMessage());
            }
            isLoading.postValue(false);
        });
    }

    public void updateItems(List<OrderItemDTO> newItems) {
        Order oldOrder = order.getValue();
        if (oldOrder != null && newItems != null) {
            Order newOrder = new Order();
            newOrder.setId(oldOrder.getId());
            newOrder.setOrderDate(oldOrder.getOrderDate());
            newOrder.setStatus(oldOrder.getStatus());
            newOrder.setStaffName(oldOrder.getStaffName());
            newOrder.setTableNames(oldOrder.getTableNames());
            newOrder.setTables(oldOrder.getTables());
            newOrder.setNote(oldOrder.getNote());

            List<OrderDetailItemDTO> detailItems = newItems.stream()
                    .map(item -> {
                        OrderDetailItemDTO detailItem = new OrderDetailItemDTO();
                        detailItem.setProductId(item.productId);
                        detailItem.setProductName(item.name);
                        detailItem.setQuantity(item.quantity);
                        detailItem.setPrice(item.unitPrice);
                        return detailItem;
                    }).collect(Collectors.toList());
            newOrder.setItems(detailItems);

            double newTotal = newItems.stream().mapToDouble(OrderItemDTO::getSubtotal).sum();
            newOrder.setTotalAmount(newTotal);

            order.setValue(newOrder);
        }
    }

    public void updateOrderOnServer(String newStatus, String newNote) {
        Order currentOrder = order.getValue();
        if (currentOrder == null) {
            updateResult.postValue(new APIResult(false, "Không có đơn hàng để cập nhật", null));
            return;
        }

        String token = authRepository.getAuthToken();
        if (token == null) {
            updateResult.postValue(new APIResult(false, "Lỗi xác thực, vui lòng đăng nhập lại", null));
            return;
        }

        List<OrderItemDTO> itemsToSave = currentOrder.getItems().stream()
                .map(i -> new OrderItemDTO(i.getProductId(), i.getProductName(), i.getPrice(), i.getQuantity(), null))
                .collect(Collectors.toList());

        OrderUpdateDTO updateData = new OrderUpdateDTO(itemsToSave, newStatus, newNote);

        isLoading.setValue(true);

        orderRepository.updateOrder(token, currentOrder.getId(), updateData, result -> {
            updateResult.postValue(result);
            isLoading.postValue(false);

            if (result.isSuccess()) {
                fetchOrderDetails(currentOrder.getId());
                if ("CANCELLED".equalsIgnoreCase(newStatus)) {
                    releaseTableStatus(currentOrder.getTables());
                }
            }
        });
    }

    private void releaseTableStatus(List<TableInfo> tables) {
        if (tables == null || tables.isEmpty()) return;

        String token = authRepository.getAuthToken();
        if (token == null) return;

        for (TableInfo table : tables) {
            tableRepository.updateTableStatus(token, table.getId(), "Available", updateResult -> {
                Log.d("OrderDetailViewModel", "Table status updated: " + table.getId());
                if (!updateResult.isSuccess()) {
                    error.postValue(updateResult.getMessage());
                }
            });
        }
    }
}
