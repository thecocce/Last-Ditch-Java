package com.gaugestructures.last_ditch.components;

import java.util.ArrayList;
import java.util.List;

public class EquippableComp extends GameComponent {
    private String slot;
    private List<String> types = new ArrayList<String>();

    public EquippableComp(List<String> types) {
        this.types = types;

        slot = types.get(0);
    }

    public List<String> getTypes() {
        return types;
    }
}
