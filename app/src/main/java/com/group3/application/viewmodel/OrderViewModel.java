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
import com.group3.application.model.entity.Product;
import com.group3.application.model.entity.User;
import com.group3.application.model.repository.OrderRepository;
import com.group3.application.model.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderViewModel extends AndroidViewModel {

    // --- Repositories ---
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // --- LiveData cho UI ---
    private final MutableLiveData<List<OrderItemDTO>> currentOrderItems;
    private final MediatorLiveData<Double> totalAmount = new MediatorLiveData<>();
    private final MutableLiveData<Event<APIResult>> _orderSubmissionResult = new MutableLiveData<>();
    public final LiveData<Event<APIResult>> orderSubmissionResult = _orderSubmissionResult;
    private final MutableLiveData<User> _currentUser = new MutableLiveData<>();
    public final LiveData<User> currentUser = _currentUser;

    // --- Thông tin Bàn/Order ---
    private List<String> tableIds;
    private String tableNames;

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

    // --- Getters ---

    public LiveData<List<OrderItemDTO>> getCurrentOrderItems() {
        return currentOrderItems;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public List<String> getTableIds() { return tableIds; }

    public String getTableNames() { return tableNames; }

    // --- Actions (Hành động từ View) ---

    public void setTableInfo(List<String> tableIds, String tableNames) {
        this.tableIds = tableIds;
        this.tableNames = tableNames;
    }
    
    // SỬA: Overload method để nhận trực tiếp OrderItemDTO
    public void addOrUpdateItem(OrderItemDTO newItem) {
        List<OrderItemDTO> currentList = currentOrderItems.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        OrderItemDTO existingItem = null;
        for (OrderItemDTO item : currentList) {
            if (Objects.equals(item.productId, newItem.productId)) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.quantity = newItem.quantity;
        } else {
            currentList.add(newItem);
        }
        // Không postValue ở đây để tránh trigger observer không cần thiết khi load order cũ
    }


    public void addOrUpdateItem(Product product, int quantity) {
        List<OrderItemDTO> currentList = currentOrderItems.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        OrderItemDTO existingItem = null;
        for (OrderItemDTO item : currentList) {
            if (Objects.equals(item.productId, product.getId())) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            if (quantity <= 0) {
                currentList.remove(existingItem);
            } else {
                existingItem.quantity = quantity;
            }
        } else if (quantity > 0) {
            OrderItemDTO newItem = new OrderItemDTO(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity
            );
            currentList.add(newItem);
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

    public void clearOrder() {
        currentOrderItems.setValue(new ArrayList<>());
        tableIds = null;
        tableNames = null;
    }

    public void submitOrder(String note) {
        List<OrderItemDTO> items = currentOrderItems.getValue();
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

    private void loadCurrentUser() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(LoginViewModel.PREF_NAME, android.content.Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(LoginViewModel.KEY_USER, null);
        if (userJson != null) {
            User user = new Gson().fromJson(userJson, User.class);
            _currentUser.setValue(user);
        }
    }
}
