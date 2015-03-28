package com.restodata.webapp.model;

public class MenuItem {
    public int id;
    public String group;
    public String name;
    public float price;

    public MenuItem(int id, String group, String name, float price) {
        this.id = id;
        this.group = group;
        this.name = name;
        this.price = price;
    }
}
