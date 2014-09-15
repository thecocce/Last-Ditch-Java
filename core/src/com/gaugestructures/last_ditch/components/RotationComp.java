package com.gaugestructures.last_ditch.components;

public class RotationComp extends Component {
    private float x = 0, y = 0;
    private float ang = 0, prevAng = 0;

    public RotationComp(float ang) {
        this.ang = this.prevAng = ang;
        x = (float)Math.cos(ang * Math.PI / 180);
        y = (float)Math.sin(ang * Math.PI / 180);
    }

    public float getAng() {
        return ang;
    }

    public void setAng(float ang) {
        this.ang = ang;
        x = (float)Math.cos(ang * Math.PI / 180);
        y = (float)Math.sin(ang * Math.PI / 180);
    }

    public float getPrevAng() {
        return prevAng;
    }

    public void setPrevAng(float prevAng) {
        this.prevAng = prevAng;
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