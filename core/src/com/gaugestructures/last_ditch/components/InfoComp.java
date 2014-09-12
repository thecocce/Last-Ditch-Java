package com.gaugestructures.last_ditch.components;

public class InfoComp extends Component {
    private String name, occupation, gender, desc;

    public InfoComp(String name) {
        this.name = name;
        occupation = "unemployed";
        gender = "female";
        desc = "";
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_occupation() {
        return occupation;
    }

    public void set_occupation(String occupation) {
        this.occupation = occupation;
    }

    public String get_gender() {
        return gender;
    }

    public void set_gender(String gender) {
        this.gender = gender;
    }

    public String get_desc() {
        return desc;
    }

    public void set_desc(String desc) {
        this.desc = desc;
    }
}
