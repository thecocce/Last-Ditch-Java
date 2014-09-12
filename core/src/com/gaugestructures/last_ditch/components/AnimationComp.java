package com.gaugestructures.last_ditch.components;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AnimationComp extends Component {
    private String cur;
    private float state_time = 0;
    private float duration = 0.1f;
    private Animation cur_anim;
    private HashMap<String, Animation> anims = new HashMap<String, Animation>();
    private HashMap<String, ArrayList<String>> names_and_frames = new HashMap<String, ArrayList<String>>();

    public AnimationComp(float duration) {
        this.duration = duration;
    }

    public void add_animation(String name, String... frames) {
        ArrayList<String> frame_list = new ArrayList<String>();

        Collections.addAll(frame_list, frames);

        names_and_frames.put(name, frame_list);
    }

    public String get_cur() {
        return cur;
    }

    public void set_cur(String cur) {
        this.cur = cur;
    }

    public void update_state_time(float dt) {
        state_time += dt;
    }

    public float get_width() {
        return get_key_frame().getRegionWidth();
    }

    public float get_height() {
        return get_key_frame().getRegionHeight();
    }

    public Animation get_cur_anim() {
        return cur_anim;
    }

    public void set_cur_anim(String name) {
        cur = name;
        cur_anim = anims.get(name);
    }

    public float get_duration() {
        return duration;
    }

    public HashMap<String, Animation> get_anims() {
        return anims;
    }

    public HashMap<String, ArrayList<String>> get_names_and_frames() {
        return names_and_frames;
    }

    public TextureRegion get_key_frame() {
        return cur_anim.getKeyFrame(state_time, true);
    }
}
