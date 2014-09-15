package com.gaugestructures.last_ditch.components;

public class PositionComp extends Component {
    private float x = 0, y = 0;
    private float px = 0, py = 0;

    public PositionComp(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getPrevX() {
        return px;
    }

    public void setPrevX(float px) {
        this.px = px;
    }

    public float getPrevY() {
        return py;
    }

    public void setPrevY(float py) {
        this.py = py;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
