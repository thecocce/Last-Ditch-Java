package com.gaugestructures.last_ditch.components;

import java.util.ArrayList;

public class InventoryComp extends GameComponent {
    private int size;
    private float weight = 0;
    private float money = 0.34f;
    private ArrayList<String> items;

    public InventoryComp(int size) {
        this.size = size;

        items = new ArrayList<String>(size);

        for (int i = 0; i < size; i++)
            items.add(null);
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

    public void modWeight(float amt) {
        weight += amt;
    }

    public float getWeight() {
        return weight;
    }

    public float getMoney() {
        return money;
    }

    public void setItem(int i, String item) {
        items.set(i, item);
    }

    public String getItem(int i) {
        return items.get(i);
    }

    public ArrayList<String> getItems() {
        return items;
    }
}
