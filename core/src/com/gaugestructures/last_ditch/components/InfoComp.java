package com.gaugestructures.last_ditch.components;

public class InfoComp extends GameComponent {
    private String name, occupation, gender, desc;

    public InfoComp(String name) {
        this.name = name;

        occupation = "unemployed";
        gender = "female";
        desc = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
