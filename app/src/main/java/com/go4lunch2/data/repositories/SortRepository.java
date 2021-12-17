package com.go4lunch2.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class SortRepository {

    private OrderBy order;
    private MutableLiveData<OrderBy> orderLiveData;

    public SortRepository() {
        this.order = OrderBy.NAME;
    }

    public LiveData<OrderBy> getOrderLiveData() {
        return orderLiveData;
    }

    public void updateOrderLiveData (OrderBy order) {
        orderLiveData.setValue(order);

    }

    public enum OrderBy {NAME, DISTANCE, RATING}





}
