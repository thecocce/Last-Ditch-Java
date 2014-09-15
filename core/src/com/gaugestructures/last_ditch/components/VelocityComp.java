package com.gaugestructures.last_ditch.components;

public class VelocityComp extends Component {
    private float spd, angSpd, maxSpd, maxAng;

    public VelocityComp(float spd, float angSpd, float maxSpd, float maxAng) {
        this.spd = spd;
        this.angSpd = angSpd;
        this.maxSpd = maxSpd;
        this.maxAng = maxAng;
    }

    public float getSpd() {
        return spd;
    }

    public void setSpd(float spd) {
        this.spd = spd;
    }

    public float getAngSpd() {
        return angSpd;
    }

    public void setAngSpd(float angSpd) {
        this.angSpd = angSpd;
    }

    public float getMaxSpd() {
        return maxSpd;
    }

    public void setMaxSpd(float maxSpd) {
        this.maxSpd = maxSpd;
    }

    public float getMaxAng() {
        return maxAng;
    }

    public void setMaxAng(float maxAng) {
        this.maxAng = maxAng;
    }
}
