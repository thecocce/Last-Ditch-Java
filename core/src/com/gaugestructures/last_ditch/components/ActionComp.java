package com.gaugestructures.last_ditch.components;

public class ActionComp extends GameComponent {
    private String type;
    private String item;

    public ActionComp(String type) {
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
