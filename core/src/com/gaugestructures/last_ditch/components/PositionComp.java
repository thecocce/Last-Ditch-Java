package com.gaugestructures.last_ditch.components;

public class PositionComp extends Component {
    private float x = 0, y = 0;
    private float px = 0, py = 0;

    public PositionComp(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float get_prev_x() {
        return px;
    }

    public void set_prev_x(float px) {
        this.px = px;
    }

    public float get_prev_y() {
        return py;
    }

    public void set_prev_y(float py) {
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
