package com.gaugestructures.last_ditch.components;

public class RotationComp extends Component {
    private float x = 0, y = 0;
    private float ang = 0, prev_ang = 0;

    public RotationComp(float ang) {
        this.ang = this.prev_ang = ang;
        x = (float)Math.cos(ang * Math.PI / 180);
        y = (float)Math.sin(ang * Math.PI / 180);
    }

    public float get_ang() {
        return ang;
    }

    public void set_ang(float ang) {
        this.ang = ang;
        x = (float)Math.cos(ang * Math.PI / 180);
        y = (float)Math.sin(ang * Math.PI / 180);
    }

    public float get_prev_ang() {
        return prev_ang;
    }

    public void set_prev_ang(float prev_ang) {
        this.prev_ang = prev_ang;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        ang = (float)Math.atan(y / x);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        ang = (float)Math.atan(y / x);
    }

    public void rotate(float amt) {
        ang += amt;
        x = (float)Math.cos(ang * Math.PI / 180);
        y = (float)Math.sin(ang * Math.PI / 180);
    }
}

