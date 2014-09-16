package com.gaugestructures.last_ditch.components;

public class StatsComp extends Component {
    private float dmg, armor;

    public StatsComp() {
        dmg = 0;
        armor = 0;
    }

    public float getDmg() {
        return dmg;
    }

    public void setDmg(float dmg) {
        this.dmg = dmg;
    }

    public float getArmor() {
        return armor;
    }

    public void setArmor(float armor) {
        this.armor = armor;
    }
}
