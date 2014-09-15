package com.gaugestructures.last_ditch.components;

public class StationComp extends Component {
    private String name, type;

    public StationComp(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
