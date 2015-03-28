package com.restodata.webapp.model;

import com.restodata.webapp.model.machine.OrderFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictResult {
    public final int year;
    public final int month;
    public final int dayOfMonth;
    public List<OrderFeature> result;

    public final Map<Integer, MenuItem> menuItems = new HashMap<Integer, MenuItem>();
    public final Map<Integer, List<Double>> results = new HashMap<Integer, List<Double>>();

    public PredictResult(PredictRequest predict) {
        year = predict.year;
        month = predict.month;
        dayOfMonth = predict.dayOfMonth;
    }

    public void add(MenuItem item, int hour, double count) {
        if (!menuItems.containsKey(item.id)) {
            menuItems.put(item.id, item);
            results.put(item.id, new ArrayList<Double>(24));
        }
        results.get(item.id).set(hour, count);
    }
}
