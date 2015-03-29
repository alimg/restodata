package com.restodata.webapp.model;

public class ApiRequest {
    public String action;

    public OrderRequest order;
    public PredictRequest predict;

    public ApiRequest(OrderRequest order) {
        action = "addOrder";
        this.order = order;
    }

    public ApiRequest(PredictRequest predict) {
        action = "getPredictions";
        this.predict = predict;
    }

    public ApiRequest(GetMenuRequest menu) {
        action = "getMenu";
    }
}
