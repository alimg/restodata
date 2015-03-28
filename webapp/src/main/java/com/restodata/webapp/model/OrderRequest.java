package com.restodata.webapp.model;

import java.util.List;

public class OrderRequest {
    public List<String> keywords;
    public String date;
    public OrderRequest(List<String> keywords, String date) {
        this.keywords = keywords;
        this.date = date;
    }
}
