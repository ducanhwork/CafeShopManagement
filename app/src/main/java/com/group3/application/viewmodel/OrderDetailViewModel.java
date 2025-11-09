package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.entity.Order;
import com.group3.application.model.repository.OrderRepository;

public class OrderDetailViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    private final MutableLiveData<Order> _order = new MutableLiveData<>();
    public final LiveData<Order> order = _order;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        this.orderRepository = new OrderRepository(application);
    }

    public void fetchOrderDetails(String orderId) {
        _isLoading.setValue(true);
        // Chú ý: Cần thêm phương thức getOrderById vào OrderRepository
        orderRepository.getOrderById(orderId, (order, error) -> {
            _isLoading.postValue(false);
            if (error != null) {
                _error.postValue(error);
            } else {
                _order.postValue(order);
            }
        });
    }
}
