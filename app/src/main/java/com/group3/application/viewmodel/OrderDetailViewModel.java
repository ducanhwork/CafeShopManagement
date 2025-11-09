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

    // SỬA: Đồng bộ lại với OrderRepository mới
    public void fetchOrderDetails(String orderId) {
        _isLoading.setValue(true);
        orderRepository.getOrderDetails(orderId, result -> {
            _isLoading.postValue(false);
            if (result.isSuccess() && result.getData() != null) {
                _order.postValue(result.getData());
            } else {
                _error.postValue(result.getMessage());
            }
        });
    }
}
