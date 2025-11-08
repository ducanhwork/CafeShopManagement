package com.group3.application.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.group3.application.common.util.Result;
import com.group3.application.common.validator.LoyaltyValidator;
import com.group3.application.common.validator.ValidationResult;
import com.group3.application.model.bean.LoyaltyMemberDetailResponse;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import com.group3.application.model.bean.PointsHistoryItem;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.model.repository.LoyaltyRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoyaltyViewModel extends AndroidViewModel {

    private final LoyaltyRepository repository;

    private final ExecutorService executor;

    private final MutableLiveData<Result<List<LoyaltyMemberListItem>>> _memberList = new MutableLiveData<>();
    public final LiveData<Result<List<LoyaltyMemberListItem>>> memberList = _memberList;

    private final MutableLiveData<Result<LoyaltyMemberDetailResponse>> _updateResult = new MutableLiveData<>();
    public final LiveData<Result<LoyaltyMemberDetailResponse>> updateResult = _updateResult;

    private final MutableLiveData<Result<List<PointsHistoryItem>>> _historyList = new MutableLiveData<>();
    public final LiveData<Result<List<PointsHistoryItem>>> historyList = _historyList;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;


    public LoyaltyViewModel(Application application) {
        super(application);
        this.repository = new LoyaltyRepository(application);

        this.executor = Executors.newSingleThreadExecutor();
    }

    public void loadMembers(String query, String sortBy) {
        _isLoading.setValue(true);

        executor.execute(() -> {
            Result<List<LoyaltyMemberListItem>> result = repository.listMembers(query, sortBy);

            _memberList.postValue(result);
            _isLoading.postValue(false);
        });
    }

    /**
     */
    public void updateMember(UUID customerId, UpdateLoyaltyMemberRequest request) {
        ValidationResult validation = LoyaltyValidator.validate(request);
        if (!validation.isValid) {
            String errorMsg = "Lỗi " + validation.errorField + ": " + validation.errorMessage;

            _updateResult.postValue(Result.failure(new IllegalArgumentException(errorMsg)));
            return;
        }

        _isLoading.setValue(true);
        executor.execute(() -> {
            Result<LoyaltyMemberDetailResponse> result = repository.editMember(customerId, request);
            _updateResult.postValue(result);
            _isLoading.postValue(false);
        });
    }

    public void loadPointsHistory(UUID customerId) {
        _isLoading.setValue(true);

        executor.execute(() -> {
            Result<List<PointsHistoryItem>> result = repository.getPointsHistory(customerId);
            _historyList.postValue(result);
            _isLoading.postValue(false);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    public void clearUpdateResult() {
        _updateResult.postValue(null);
    }
}
