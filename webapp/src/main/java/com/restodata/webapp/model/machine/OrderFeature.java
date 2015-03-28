package com.restodata.webapp.model.machine;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;

public class OrderFeature {

    public static final int FEATURE_COUNT = 6;
    public int itemId;
    public int year;
    public int month;
    public int dayOfMonth;
    public int dayOfWeek;
    public int hour;
    public int count;

    public OrderFeature(int itemId, int year, int month, int dayOfMonth, int dayOfWeek, int hour, int count) {
        this.itemId = itemId;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.count = count;
    }

    public boolean matches(OrderFeature o) {
        return itemId == o.itemId && year == o.year &&
                month == o.month && dayOfMonth == o.dayOfMonth &&
                dayOfWeek == o.dayOfWeek && hour == o.hour;
    }

    public String toCsv() {
        StringBuilder b = new StringBuilder();
        b.append(itemId).append(",");
        b.append(year).append(",");
        b.append(month).append(",");
        b.append(dayOfMonth).append(",");
        b.append(dayOfWeek).append(",");
        b.append(hour).append(",");
        b.append(count);
        return b.toString();
    }

    public Feature[] toFeatures() {
        return new Feature[]{ new FeatureNode(0, itemId),
                new FeatureNode(1, year),
                new FeatureNode(2, month),
                new FeatureNode(3, dayOfMonth),
                new FeatureNode(4, dayOfWeek),
                new FeatureNode(5, hour)};
    }
}
