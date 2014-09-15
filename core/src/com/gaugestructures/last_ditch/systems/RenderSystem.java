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

        for (String entity : mgr.entitiesWith(RenderComp.class)) {
            RenderComp render_comp = mgr.comp(entity, RenderComp.class);
            render_comp.setRegion(atlas.findRegion(render_comp.getRegionName()));

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

        for (String entity : mgr.entitiesWith(AnimationComp.class)) {
            boolean first = true;

            AnimationComp anim_comp = mgr.comp(entity, AnimationComp.class);

            Iterator it = anim_comp.getNamesAndFrames().entrySet().iterator();

            while (it.hasNext()) {
                Array<TextureRegion> frame_list = new Array<TextureRegion>();

                Map.Entry pairs = (Map.Entry)it.next();

                @SuppressWarnings("unchecked")
                ArrayList<String> frames = (ArrayList<String>)pairs.getValue();

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

                anim_comp.getAnims().put(
                    (String) pairs.getKey(),
                    new Animation(anim_comp.getDuration(), frame_list)
                );

                if (first) {
                    first = false;
                    anim_comp.setCur((String) pairs.getKey());
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

            for (String entity : mgr.entitiesWith(PositionComp.class)) {
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

        for (String entity : mgr.entitiesWith(VelocityComp.class)) {
            AnimationComp anim_comp = mgr.comp(entity, AnimationComp.class);
            CollisionComp col_comp = mgr.comp(entity, CollisionComp.class);

            anim_comp.updateStateTime(C.BOX_STEP);
            Vector2 vel_vec = col_comp.getBody().getLinearVelocity();

            if (entity.equals(player)) {
                InfoComp info_comp = mgr.comp(player, InfoComp.class);

                if (Math.abs(vel_vec.x) < 0.02f && Math.abs(vel_vec.y) < 0.02f) {
                    anim_comp.setCur(String.format("%s1/idle", info_comp.getGender()));
                } else if (!anim_comp.getCur().equals(String.format("%s1/walk", info_comp.getGender()))) {
                    anim_comp.setCur(String.format("%s1/walk", info_comp.getGender()));
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        for(String entity : nearby_entities) {
            if(mgr.hasComp(entity, RenderComp.class)) {
                PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
                RotationComp rot_comp = mgr.comp(entity, RotationComp.class);
                SizeComp size_comp = mgr.comp(entity, SizeComp.class);
                RenderComp render_comp = mgr.comp(entity, RenderComp.class);
                TypeComp type_comp = mgr.comp(entity, TypeComp.class);

                batch.draw(
                    render_comp.getRegion(),
                    C.BTW * (pos_comp.getX() - size_comp.getW() / 2),
                    C.BTW * (pos_comp.getY() - size_comp.getH() / 2),
                    C.BTW * size_comp.getW() / 2, C.BTW * size_comp.getH() / 2,
                    C.BTW * size_comp.getW(), C.BTW * size_comp.getH(),
                    render_comp.getScale(), render_comp.getScale(),
                    rot_comp.getAng()
                );
            } else if(mgr.hasComp(entity, AnimationComp.class)) {
                PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
                RotationComp rot_comp = mgr.comp(entity, RotationComp.class);
                AnimationComp anim_comp = mgr.comp(entity, AnimationComp.class);

                batch.draw(
                    anim_comp.getKeyFrame(),
                    C.BTW * pos_comp.getX() - anim_comp.getW() / 2,
                    C.BTW * pos_comp.getY() - anim_comp.getH() / 2,
                    anim_comp.getW() / 2, anim_comp.getH() / 2,
                    anim_comp.getW(), anim_comp.getH(),
                    anim_comp.getScale(), anim_comp.getScale(),
                    rot_comp.getAng()
                );
            }
        }

        AnimationComp anim_comp = mgr.comp(player, AnimationComp.class);
        PositionComp pos_comp = mgr.comp(player, PositionComp.class);
        RotationComp rot_comp = mgr.comp(player, RotationComp.class);

        batch.draw(
            anim_comp.getKeyFrame(),
            C.BTW * pos_comp.getX() - anim_comp.getW() / 2,
            C.BTW * pos_comp.getY() - anim_comp.getH() / 2,
            anim_comp.getW() / 2, anim_comp.getH() / 2,
            anim_comp.getW(), anim_comp.getH(),
            anim_comp.getScale(), anim_comp.getScale(),
            rot_comp.getAng()
        );
    }
}
