package com.gaugestructures.last_ditch.components;

public class AIComp extends GameComponent {
    private String type;

    public AIComp(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
