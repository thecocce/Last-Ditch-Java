package com.gaugestructures.last_ditch.components;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AnimationComp extends Component {
    private String cur;
    private float scale = 1f;
    private float stateTime = 0;
    private float duration = 0.1f;
    private Animation curAnim;
    private HashMap<String, Animation> anims = new HashMap<String, Animation>();
    private HashMap<String, ArrayList<String>> namesAndFrames = new HashMap<String, ArrayList<String>>();

    public AnimationComp(float duration) {
        this.duration = duration;
    }

    public void addAnimation(String name, String... frames) {
        ArrayList<String> frameList = new ArrayList<String>();

        Collections.addAll(frameList, frames);

        namesAndFrames.put(name, frameList);
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
        curAnim = anims.get(cur);
    }

    public void updateStateTime(float dt) {
        stateTime += dt;
    }

    public float getW() {
        return getKeyFrame().getRegionWidth();
    }

    public float getH() {
        return getKeyFrame().getRegionHeight();
    }

    public float getDuration() {
        return duration;
    }

    public HashMap<String, Animation> getAnims() {
        return anims;
    }

    public HashMap<String, ArrayList<String>> getNamesAndFrames() {
        return namesAndFrames;
    }

    public TextureRegion getKeyFrame() {
        return curAnim.getKeyFrame(stateTime, true);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
