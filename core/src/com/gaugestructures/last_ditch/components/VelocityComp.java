package com.gaugestructures.last_ditch.components;

public class VelocityComp extends Component {
    private float spd, ang_spd, max_spd, max_ang;

    public VelocityComp(float spd, float ang_spd, float max_spd, float max_ang) {
        this.spd = spd;
        this.ang_spd = ang_spd;
        this.max_spd = max_spd;
        this.max_ang = max_ang;
    }

    public float get_spd() {
        return spd;
    }

    public void set_spd(float spd) {
        this.spd = spd;
    }

    public float get_ang_spd() {
        return ang_spd;
    }

    public void set_ang_spd(float ang_spd) {
        this.ang_spd = ang_spd;
    }

    public float get_max_spd() {
        return max_spd;
    }

    public void set_max_spd(float max_spd) {
        this.max_spd = max_spd;
    }

    public float get_max_ang() {
        return max_ang;
    }

    public void set_max_ang(float max_ang) {
        this.max_ang = max_ang;
    }
}
