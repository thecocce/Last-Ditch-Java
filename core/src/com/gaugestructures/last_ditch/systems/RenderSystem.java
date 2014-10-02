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
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class RenderSystem extends GameSystem {
    private Manager mgr;
    private MapSystem map;
    private PositionComp focus;

    public RenderSystem(Manager mgr, MapSystem map) {
        this.mgr = mgr;
        this.map = map;
        focus = map.getFocus();

        for (String entity : mgr.entitiesWith(RenderComp.class)) {
            RenderComp renderComp = mgr.comp(entity, RenderComp.class);
            renderComp.setRegion(mgr.getAtlas().findRegion(renderComp.getRegionName()));

            PositionComp posComp = mgr.comp(entity, PositionComp.class);
            PositionComp playerPosComp = mgr.comp(mgr.getPlayer(), PositionComp.class);

            float x_dist = posComp.getX() - playerPosComp.getX();
            float y_dist = posComp.getY() - playerPosComp.getY();
        }

        for (String entity : mgr.entitiesWith(AnimationComp.class)) {
            boolean first = true;
            AnimationComp animComp = mgr.comp(entity, AnimationComp.class);
            Iterator it = animComp.getNamesAndFrames().entrySet().iterator();

            while (it.hasNext()) {
                Array<TextureRegion> frameList = new Array<TextureRegion>();
                Map.Entry pairs = (Map.Entry)it.next();

                @SuppressWarnings("unchecked")
                ArrayList<String> frames = (ArrayList<String>)pairs.getValue();

                for (String frame : frames) {
                    if (frame.endsWith("-f")) {
                        frame = frame.replace("-f", "");
                        TextureRegion region = new TextureRegion(mgr.getAtlas().findRegion(frame));
                        region.flip(false, true);
                        frameList.add(region);
                    } else {
                        frameList.add(mgr.getAtlas().findRegion(frame));
                    }
                }

                animComp.getAnims().put(
                    (String)pairs.getKey(),
                    new Animation(animComp.getDuration(), frameList));

                if (first) {
                    first = false;
                    animComp.setCur((String)pairs.getKey());
                }

                it.remove();
            }
        }
    }

    public void update() {
        for (String entity : mgr.entitiesWith(VelocityComp.class)) {
            AnimationComp animComp = mgr.comp(entity, AnimationComp.class);
            CollisionComp colComp = mgr.comp(entity, CollisionComp.class);

            animComp.updateStateTime(C.BOX_STEP);
            Vector2 velVec = colComp.getBody().getLinearVelocity();

            if (entity.equals(mgr.getPlayer())) {
                InfoComp infoComp = mgr.comp(mgr.getPlayer(), InfoComp.class);

                if (Math.abs(velVec.x) < 0.02f && Math.abs(velVec.y) < 0.02f) {
                    animComp.setCur(String.format("%s1/idle", infoComp.getGender()));
                } else if (!animComp.getCur().equals(String.format("%s1/walk", infoComp.getGender()))) {
                    animComp.setCur(String.format("%s1/walk", infoComp.getGender()));
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int cx = (int)focus.getX() - C.CHUNK_SIZE; cx <= focus.getX() + C.CHUNK_SIZE; cx += C.CHUNK_SIZE) {
            for (int cy = (int)focus.getY() - C.CHUNK_SIZE; cy <= focus.getY() + C.CHUNK_SIZE; cy += C.CHUNK_SIZE) {
                int chunk = map.getChunk(cx, cy);

                if (chunk != -1) {
                    Iterable<String> entities = Iterables.concat(
                        map.getDoors().get(chunk), map.getItems().get(chunk));

                    for (String entity : entities) {
                        if (mgr.hasComp(entity, RenderComp.class)) {
                            PositionComp posComp = mgr.comp(entity, PositionComp.class);
                            RotationComp rotComp = mgr.comp(entity, RotationComp.class);
                            SizeComp sizeComp = mgr.comp(entity, SizeComp.class);
                            RenderComp renderComp = mgr.comp(entity, RenderComp.class);

                            batch.draw(
                                renderComp.getRegion(),
                                C.BTW * (posComp.getX() - sizeComp.getW() / 2),
                                C.BTW * (posComp.getY() - sizeComp.getH() / 2),
                                C.BTW * sizeComp.getW() / 2, C.BTW * sizeComp.getH() / 2,
                                C.BTW * sizeComp.getW(), C.BTW * sizeComp.getH(),
                                renderComp.getScale(), renderComp.getScale(),
                                rotComp.getAng());
                        }
                    }
                }
            }
        }

        AnimationComp animComp = mgr.comp(mgr.getPlayer(), AnimationComp.class);
        PositionComp posComp = mgr.comp(mgr.getPlayer(), PositionComp.class);
        RotationComp rotComp = mgr.comp(mgr.getPlayer(), RotationComp.class);

        batch.draw(
            animComp.getKeyFrame(),
            C.BTW * posComp.getX() - animComp.getW() / 2,
            C.BTW * posComp.getY() - animComp.getH() / 2,
            animComp.getW() / 2, animComp.getH() / 2,
            animComp.getW(), animComp.getH(),
            animComp.getScale(), animComp.getScale(),
            rotComp.getAng());
    }
}
