package com.gaugestructures.last_ditch.components;

public class TypeComp extends Component {
    private String type;

    public TypeComp(String type) {
        this.type = type;
    }

    public String get_type() {
        return type;
    }

    public void set_type(String type) {
        this.type = type;
    }
}
