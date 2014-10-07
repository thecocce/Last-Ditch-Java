package com.gaugestructures.last_ditch.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RenderComp extends GameComponent {
    private float scale = 1f;
    private String regionName;

    private TextureRegion region;

    public RenderComp(String regionName) {
        this.regionName = regionName;
    }

    public RenderComp(String regionName, TextureRegion region) {
        this.regionName = regionName;
        this.region = region;
    }

    public float getW() {
        return region.getRegionWidth();
    }

    public float getH() {
        return region.getRegionHeight();
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
