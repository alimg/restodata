package com.restodata.webapp.model;

import java.util.ArrayList;
import java.util.List;

public class MenuResponse {
    public List<MenuItem> items;

    public MenuResponse(ArrayList<MenuItem> items) {
        this.items = items;
    }
}
