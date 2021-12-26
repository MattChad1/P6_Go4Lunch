package com.go4lunch2.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class SortRepository {

    private OrderBy order;
    private MutableLiveData<OrderBy> orderLiveData = new MutableLiveData<>();

    public SortRepository() {

        this.order = OrderBy.NAME;
        orderLiveData.setValue(order);
    }

    public LiveData<OrderBy> getOrderLiveData() {
        return orderLiveData;
    }

    public void updateOrderLiveData (OrderBy order) {
        orderLiveData.setValue(order);

    }

    public enum OrderBy {NAME, DISTANCE, RATING}

}
