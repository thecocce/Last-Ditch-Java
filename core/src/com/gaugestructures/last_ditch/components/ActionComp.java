package com.gaugestructures.last_ditch.components;

public class ActionComp extends GameComponent {
    private String type;

    public ActionComp(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
