package com.group3.application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group3.application.model.entity.Order;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.model.entity.User;
import com.group3.application.model.repository.OrderRepository;
import com.group3.application.model.repository.TableRepository;
import com.group3.application.model.repository.UserRepository;

import java.util.List;

public class OrderListViewModel extends AndroidViewModel {

    // Repositories
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;

    // LiveData cho danh sách Order
    private final MutableLiveData<List<Order>> _orders = new MutableLiveData<>();
    public final LiveData<List<Order>> orders = _orders;

    // LiveData cho dữ liệu bộ lọc
    private final MutableLiveData<List<User>> _users = new MutableLiveData<>();
    public final LiveData<List<User>> users = _users;
    private final MutableLiveData<List<TableInfo>> _tables = new MutableLiveData<>();
    public final LiveData<List<TableInfo>> tables = _tables;

    // LiveData cho trạng thái UI
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    // Biến lưu trạng thái bộ lọc
    private String statusFilter = null;
    private String tableIdFilter = null;
    private String staffIdFilter = null;

    public OrderListViewModel(@NonNull Application application) {
        super(application);
        this.orderRepository = new OrderRepository(application);
        this.userRepository = new UserRepository(application);
        this.tableRepository = new TableRepository(); // Không cần Application context
    }

    // Lấy dữ liệu cho các bộ lọc (dropdown)
    public void fetchFilterData(){
        userRepository.getAllUsers((users, error) -> {
            if(error != null) _error.postValue("Lỗi lấy danh sách NV: " + error);
            else _users.postValue(users);
        });

        tableRepository.getAllTables((tables, error) -> {
            if(error != null) _error.postValue("Lỗi lấy danh sách bàn: " + error);
            else _tables.postValue(tables);
        });
    }

    // SỬA: Đồng bộ lại với OrderRepository mới
    public void fetchOrders() {
        _isLoading.setValue(true);
        orderRepository.getOrders(statusFilter, tableIdFilter, staffIdFilter, result -> {
            _isLoading.postValue(false);
            if (result.isSuccess() && result.getData() != null) {
                _orders.postValue(result.getData());
            } else {
                _error.postValue(result.getMessage());
            }
        });
    }

    // Hàm để Activity cập nhật bộ lọc và gọi lại fetchOrders
    public void setStatusFilter(@Nullable String status) {
        // Nếu chọn "ALL", giá trị gửi đi là null
        this.statusFilter = (status != null && status.equalsIgnoreCase("ALL")) ? null : status;
        fetchOrders();
    }

    public void setTableIdFilter(@Nullable String tableId) {
        this.tableIdFilter = tableId;
        fetchOrders();
    }

    public void setStaffIdFilter(@Nullable String staffId) {
        this.staffIdFilter = staffId;
        fetchOrders();
    }
}
