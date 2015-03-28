package com.restodata.webapp.model;

import java.util.Calendar;

public class PredictRequest {
    public int year;
    public int month;
    public int dayOfMonth;

    public int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public PredictRequest(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }
}



