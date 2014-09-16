package com.gaugestructures.last_ditch.components;

import java.util.ArrayList;

public class EquippableComp extends Component {
    private String slot;
    private ArrayList<String> types = new ArrayList<String>();

    public EquippableComp(ArrayList<String> types) {
        this.types = types;
        slot = types.get(0);
    }

    public ArrayList<String> getTypes() {
        return types;
    }
}
