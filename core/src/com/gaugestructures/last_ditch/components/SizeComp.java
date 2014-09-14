package com.gaugestructures.last_ditch.components;

public class SizeComp extends Component {
    private float w = 0, h = 0;

    public SizeComp(float w, float h) {
        this.w = w;
        this.h = h;
    }

    public float getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public float getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
