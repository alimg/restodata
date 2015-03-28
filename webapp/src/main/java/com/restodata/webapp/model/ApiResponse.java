package com.restodata.webapp.model;

public class ApiResponse {
    public PredictResult predictResult;
    public MenuItem matchedItem;
    public MenuResponse menu;
    public String status;

    public ApiResponse(String status, MenuResponse menu) {
        this(status);
        this.menu = menu;
    }

    public ApiResponse(String status) {
        this.status = status;
    }

    public ApiResponse(String status, MenuItem item) {
        this(status);
        this.matchedItem = item;
    }

    public ApiResponse(String status, PredictResult predictResult) {
        this(status);
        this.predictResult = predictResult;
    }
}
