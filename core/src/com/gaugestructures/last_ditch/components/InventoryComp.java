package com.gaugestructures.last_ditch.components;

import java.util.ArrayList;

public class InventoryComp extends Component {
    private int size;
    private float weight = 0;
    private float money = 0.34f;
    private String[] items;

    public InventoryComp(int size) {
        this.size = size;
        items = new String[size];
    }

    public void addMoney(float amt) {
        money += amt;
    }

    public boolean removeMoney(float amt) {
        if(money > 0) {
            money -= amt;
            return true;
        } else {
            return false;
        }
    }

    public int getSize() {
        return size;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public float getMoney() {
        return money;
    }

    public void setItem(int i, String item) {
        items[i] = item;
    }

    public String getItem(int i) {
        return items[i];
    }

    public String[] getItems() {
        return items;
    }
}
