package com.gaugestructures.last_ditch.components;

public class SizeComp extends Component {
    private int w = 0, h = 0;

    private SizeComp(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
