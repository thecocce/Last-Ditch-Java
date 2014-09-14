package com.gaugestructures.last_ditch.components;

public class InventoryComp extends Component {
    private int size;
    private float weight = 0;
    private float money = 0.34f;
    private String[] items;

    public InventoryComp(int size) {
        this.size = size;
        items = new String[size];
    }

    public void add_money(float amt) {
        money += amt;
    }

    public boolean remove_money(float amt) {
        if(money > 0) {
            money -= amt;
            return true;
        } else {
            return false;
        }
    }

    public int get_size() {
        return size;
    }

    public float get_weight() {
        return weight;
    }

    public float get_money() {
        return money;
    }

    public String get_item(int i) {
        return items[i];
    }
}
