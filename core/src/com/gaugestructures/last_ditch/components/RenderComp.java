package com.gaugestructures.last_ditch.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderComp extends Component {
    private float scale = 1f;
    private String region_name;
    private TextureRegion region;

    public RenderComp(String region_name) {
        this.region_name = region_name;
    }

    public RenderComp(String region_name, TextureRegion region) {
        this.region_name = region_name;
        this.region = region;
    }

    public String get_region_name() {
        return region_name;
    }

    public void set_region_name(String region_name) {
        this.region_name = region_name;
    }

    public TextureRegion get_region() {
        return region;
    }

    public void set_region(TextureRegion region) {
        this.region = region;
    }

    public float get_scale() {
        return scale;
    }

    public void set_scale(float scale) {
        this.scale = scale;
    }
}
