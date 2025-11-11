package com.group3.application.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.OrderDetailItemDTO;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.dto.OrderUpdateDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDetailViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    public final MutableLiveData<Order> order = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public final MutableLiveData<APIResult> updateResult = new MutableLiveData<>();

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }

    public void fetchOrderDetails(String orderId) {
        isLoading.setValue(true);
        orderRepository.getOrderDetails(orderId, result -> {
            if (result.isSuccess() && result.getData() != null) {
                order.postValue(result.getData());
            } else {
                error.postValue(result.getMessage());
            }
            isLoading.postValue(false);
        });
    }

    // Logic này đã đúng: Tạo một đối tượng Order mới để đảm bảo LiveData được trigger
    public void updateItems(List<OrderItemDTO> newItems) {
        Order oldOrder = order.getValue();
        if (oldOrder != null && newItems != null) {
            Order newOrder = new Order();

            // Sao chép thuộc tính cũ
            newOrder.setId(oldOrder.getId());
            newOrder.setOrderDate(oldOrder.getOrderDate());
            newOrder.setStatus(oldOrder.getStatus());
            newOrder.setStaffName(oldOrder.getStaffName());
            newOrder.setTableNames(oldOrder.getTableNames());
            newOrder.setTables(oldOrder.getTables());
            newOrder.setNote(oldOrder.getNote());

            // Chuyển đổi và đặt danh sách items mới
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

            // Đặt tổng tiền mới
            double newTotal = newItems.stream().mapToDouble(OrderItemDTO::getSubtotal).sum();
            newOrder.setTotalAmount(newTotal);

            // Dùng setValue để cập nhật ngay lập tức
            order.setValue(newOrder);
        }
    }

    public void updateOrderOnServer(String newStatus, String newNote) {
        Order currentOrder = order.getValue();
        if (currentOrder == null) {
            updateResult.postValue(new APIResult(false, "Không có đơn hàng để cập nhật", null));
            return;
        }

        // 1. Lấy danh sách món ăn (đã được cập nhật từ hàm updateItems)
        List<OrderItemDTO> itemsToSave = currentOrder.getItems().stream()
                .map(i -> new OrderItemDTO(
                        i.getProductId(),
                        i.getProductName(),
                        i.getPrice(),
                        i.getQuantity(),
                        null
                ))
                .collect(Collectors.toList());

        // 2. Tạo DTO mới với đầy đủ 3 thông tin
        OrderUpdateDTO updateData = new OrderUpdateDTO(itemsToSave, newStatus, newNote);

        isLoading.setValue(true);

        // 3. Gọi Repository
        orderRepository.updateOrder(currentOrder.getId(), updateData, result -> {
            updateResult.postValue(result);
            isLoading.postValue(false);

            // Nếu thành công, nên tải lại dữ liệu để đảm bảo đồng bộ
            if (result.isSuccess()) {
                fetchOrderDetails(currentOrder.getId());
            }
        });
    }

    public void updateTables(List<TableInfo> newTables) {
        Order oldOrder = order.getValue();
        if (oldOrder != null && newTables != null) {
            // 1. Tạo một đối tượng Order mới để kích hoạt LiveData
            Order newOrder = new Order();

            // 2. Sao chép các thuộc tính cũ
            newOrder.setId(oldOrder.getId());
            newOrder.setOrderDate(oldOrder.getOrderDate());
            newOrder.setStatus(oldOrder.getStatus());
            newOrder.setStaffName(oldOrder.getStaffName());
            newOrder.setTotalAmount(oldOrder.getTotalAmount());
            newOrder.setNote(oldOrder.getNote());
            newOrder.setItems(oldOrder.getItems()); // <-- Giữ nguyên danh sách MÓN ĂN

            // 3. Cập nhật danh sách BÀN mới
            newOrder.setTables(newTables);

            // 4. Cập nhật danh sách TÊN BÀN mới
            List<String> tableNames = newTables.stream()
                    .map(TableInfo::getName) // Giả sử TableInfo có hàm getName()
                    .collect(Collectors.toList());
            newOrder.setTableNames(tableNames);

            // 5. Kích hoạt LiveData
            order.setValue(newOrder);
        }
    }
}
