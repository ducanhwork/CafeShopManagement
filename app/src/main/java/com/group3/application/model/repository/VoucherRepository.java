package com.group3.application.model.repository;

import com.group3.application.model.bean.VoucherRequest;
import com.group3.application.model.bean.VoucherResponse;
import com.group3.application.model.webservice.ApiClient;
import com.group3.application.model.webservice.ApiService;

import java.util.List;
import java.util.Map;
import retrofit2.Call;

public class VoucherRepository {
    private final ApiService api;

    public VoucherRepository() {
        this.api = ApiClient.getApi();
    }

    public Call<VoucherResponse> create(VoucherRequest req) {
        return api.createVoucher(req);
    }

    public Call<List<VoucherResponse>> list(String codeLike, String status, String type, String sortBy){
        return api.listVouchers(empty(codeLike), empty(status), empty(type), empty(sortBy));
    }

    public Call<VoucherResponse> get(String id){
        return api.getVoucher(id);
    }

    public Call<VoucherResponse> patch(String id, Map<String, Object> fields){
        return api.patchVoucher(id, fields);
    }

    private String empty(String s){ return (s==null || s.trim().isEmpty())? null : s.trim(); }
}
