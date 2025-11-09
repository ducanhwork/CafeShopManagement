package com.group3.application.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.group3.application.common.utils.Event;
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.Product;
import com.group3.application.model.entity.User;
import com.group3.application.model.repository.OrderRepository;
import com.group3.application.model.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<List<OrderItemDTO>> currentOrderItems;
    private final MediatorLiveData<Double> totalAmount = new MediatorLiveData<>();
    private final MutableLiveData<Event<APIResult>> _orderSubmissionResult = new MutableLiveData<>();
    public final LiveData<Event<APIResult>> orderSubmissionResult = _orderSubmissionResult;
    private final MutableLiveData<User> _currentUser = new MutableLiveData<>();
    public final LiveData<User> currentUser = _currentUser;

    private List<String> tableIds;
    private String tableNames;
    private String editOrderId;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        this.orderRepository = new OrderRepository(application);
        this.userRepository = new UserRepository(application);
        this.currentOrderItems = new MutableLiveData<>(new ArrayList<>());

        totalAmount.addSource(currentOrderItems, items -> {
            double total = 0;
            if (items != null) {
                for (OrderItemDTO item : items) {
                    total += item.getSubtotal();
                }
            }
            totalAmount.setValue(total);
        });

        loadCurrentUser();
    }

    public LiveData<List<OrderItemDTO>> getCurrentOrderItems() { return currentOrderItems; }
    public LiveData<Double> getTotalAmount() { return totalAmount; }
    public List<String> getTableIds() { return tableIds; }
    public String getTableNames() { return tableNames; }

    public void setTableInfo(List<String> tableIds, String tableNames) {
        this.tableIds = tableIds;
        this.tableNames = tableNames;
    }

    public void loadExistingOrder(String orderId) {
        this.editOrderId = orderId;
        orderRepository.getOrderDetails(orderId, result -> {
            if (result.isSuccess() && result.getData() != null) {
                Order order = result.getData();
                // SỬA: Dùng đúng tên trường khi map dữ liệu từ server
                List<OrderItemDTO> existingItems = order.getItems().stream()
                        .map(item -> new OrderItemDTO(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity()))
                        .collect(Collectors.toList());
                currentOrderItems.postValue(existingItems);
                setTableInfo(order.getTableIds(), String.join(", ", order.getTableNames()));
            }
        });
    }

    // SỬA: Sử dụng đúng tên trường "name"
    public void addOrUpdateItem(Product product, int quantity) {
        List<OrderItemDTO> currentList = currentOrderItems.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        OrderItemDTO existingItem = currentList.stream()
                .filter(item -> Objects.equals(item.name, product.getName()))
                .findFirst().orElse(null);

        if (existingItem != null) {
            if (quantity <= 0) {
                currentList.remove(existingItem);
            } else {
                existingItem.quantity = quantity;
            }
        } else if (quantity > 0) {
            currentList.add(new OrderItemDTO(product.getId(), product.getName(), product.getPrice(), quantity));
        }
        currentOrderItems.setValue(currentList);
    }

    public void updateQuantityForSummary(OrderItemDTO itemToUpdate, int newQuantity) {
        List<OrderItemDTO> currentList = currentOrderItems.getValue();
        if (currentList == null) return;
        if (newQuantity <= 0) {
            currentList.remove(itemToUpdate);
        } else {
            itemToUpdate.quantity = newQuantity;
        }
        currentOrderItems.setValue(currentList);
    }

    public void submitOrder(String note) {
        List<OrderItemDTO> items = currentOrderItems.getValue();

        if (editOrderId != null) {
            if (items == null) items = new ArrayList<>();
            orderRepository.updateOrderItems(editOrderId, items, note, result -> {
                if (result.isSuccess()) {
                    clearOrder();
                }
                _orderSubmissionResult.postValue(new Event<>(result));
            });
        } else {
            if (tableIds == null || tableIds.isEmpty() || items == null || items.isEmpty()) {
                _orderSubmissionResult.postValue(new Event<>(new APIResult(false, "Vui lòng chọn bàn và món ăn.", null)));
                return;
            }
            orderRepository.createOrder(tableIds, items, note, result -> {
                if (result.isSuccess()) {
                    clearOrder();
                }
                _orderSubmissionResult.postValue(new Event<>(result));
            });
        }
    }

    public void clearOrder() {
        currentOrderItems.setValue(new ArrayList<>());
        tableIds = null;
        tableNames = null;
        editOrderId = null;
    }

    private void loadCurrentUser() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(LoginViewModel.PREF_NAME, android.content.Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(LoginViewModel.KEY_USER, null);
        if (userJson != null) {
            _currentUser.setValue(new Gson().fromJson(userJson, User.class));
        }
    }
}
