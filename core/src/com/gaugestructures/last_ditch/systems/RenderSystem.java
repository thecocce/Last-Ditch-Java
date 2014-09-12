package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class RenderSystem extends GameSystem {
    private Manager mgr;
    private String player;
    private int update_timer = 0;
    private TextureAtlas atlas;
    private ArrayList<String> nearby_entities = new ArrayList<String>();

    public RenderSystem(Manager mgr, String player, TextureAtlas atlas) {
        this.mgr = mgr;
        this.player = player;
        this.atlas = atlas;

        for (String entity : mgr.entities_with(RenderComp.class)) {
            RenderComp render_comp = mgr.comp(player, RenderComp.class);
            render_comp.set_region(atlas.findRegion(render_comp.get_region_name()));

            PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
            PositionComp player_pos_comp = mgr.comp(player, PositionComp.class);

            float x_dist = pos_comp.getX() - player_pos_comp.getX();
            float y_dist = pos_comp.getY() - player_pos_comp.getY();

            if (-20 < x_dist && x_dist < 20) {
                if (-20 < y_dist && y_dist < 20) {
                    nearby_entities.add(entity);
                }
            }
        }

        for (String entity : mgr.entities_with(AnimationComp.class)) {
            boolean first = true;

            AnimationComp anim_comp = mgr.comp(entity, AnimationComp.class);

            Iterator it = anim_comp.get_names_and_frames().entrySet().iterator();

            while (it.hasNext()) {
                Array<TextureRegion> frame_list = new Array<TextureRegion>();

                Map.Entry pairs = (Map.Entry) it.next();

                @SuppressWarnings("unchecked")
                ArrayList<String> frames = (ArrayList<String>) pairs.getValue();

                for (String frame : frames) {
                    if (frame.endsWith("-f")) {
                        frame = frame.replace("-f", "");
                        TextureRegion region = new TextureRegion(atlas.findRegion(frame));
                        region.flip(false, true);
                        frame_list.add(region);
                    } else {
                        frame_list.add(atlas.findRegion(frame));
                    }
                }

                anim_comp.get_anims().put(
                    (String) pairs.getKey(),
                    new Animation(anim_comp.get_duration(), frame_list));

                if (first) {
                    first = false;
                    anim_comp.set_cur_anim((String) pairs.getKey());

                }
                it.remove();
            }
        }
    }

    public void update() {
        if (update_timer < 100) {
            update_timer += 1;
        } else {
            update_timer = 0;
            nearby_entities.clear();

            for (String entity : mgr.entities_with(PositionComp.class)) {
                PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
                PositionComp player_pos_comp = mgr.comp(player, PositionComp.class);

                float x_dist = pos_comp.getX() - player_pos_comp.getX();
                float y_dist = pos_comp.getY() - player_pos_comp.getY();

                if (-20 < x_dist && x_dist < 20) {
                    if (-20 < y_dist && y_dist < 20) {
                        nearby_entities.add(entity);
                    }
                }
            }
        }

        for (String entity : mgr.entities_with(VelocityComp.class)) {
            AnimationComp anim_comp = mgr.comp(entity, AnimationComp.class);
            CollisionComp col_comp = mgr.comp(entity, CollisionComp.class);

            anim_comp.update_state_time(C.BOX_STEP);
            Vector2 vel_vec = col_comp.get_body().getLinearVelocity();

            if (entity.equals(player)) {
                InfoComp info_comp = mgr.comp(player, InfoComp.class);

                if (Math.abs(vel_vec.x) < 0.02f && Math.abs(vel_vec.y) < 0.02f) {
                    anim_comp.set_cur(String.format("%s1/idle", info_comp.get_gender()));
                } else if (!anim_comp.get_cur().equals(String.format("%s1/walk", info_comp.get_gender()))) {
                    anim_comp.set_cur(String.format("%s1/walk", info_comp.get_gender()));
                }

            }
        }
    }

    public void render(SpriteBatch batch) {

    }
}
