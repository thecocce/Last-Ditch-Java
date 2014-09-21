package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gaugestructures.last_ditch.C;
import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.*;

import java.util.ArrayList;
import java.util.Set;

public class PhysicsSystem extends GameSystem {
    private Manager mgr;
    private String player;
    private MapSystem map;
    private Body player_body;
    private ArrayList<Body> bodies = new ArrayList<Body>();
    private Vector2 gravity = new Vector2(0, 0);
    private World world = new World(gravity, false);

    public PhysicsSystem(Manager mgr, String player, MapSystem map) {
        this.mgr = mgr;
        this.player = player;
        this.map = map;

        generate_entity_bodies();
        generate_tile_bodies();
        generate_door_bodies();
        genderate_station_bodies();
    }

    private void generate_entity_bodies() {
        for (String entity : mgr.entitiesWithAll(AnimationComp.class, CollisionComp.class)) {
            PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
            AnimationComp anim_comp = mgr.comp(entity, AnimationComp.class);
            CollisionComp col_comp = mgr.comp(entity, CollisionComp.class);

            float w = anim_comp.getW() * C.WTB;
            float h = anim_comp.getH() * C.WTB;

            BodyDef body_def = new BodyDef();
            body_def.type = BodyDef.BodyType.DynamicBody;
            body_def.linearDamping = 20.0f;
            body_def.position.set(pos_comp.getX() + w/2, pos_comp.getY() + h/2);

            CircleShape shape = new CircleShape();
            shape.setRadius(w/2 - 0.01f);

            FixtureDef fixture_def = new FixtureDef();
            fixture_def.shape = shape;
            fixture_def.friction = 0.2f;
            fixture_def.density = 1f;

            if (entity.equals(player)) {
                fixture_def.filter.categoryBits = C.BIT_PLAYER;
            } else {
                fixture_def.filter.categoryBits = C.BIT_ENTITY;
            }

            col_comp.setBody(world.createBody(body_def));
            col_comp.getBody().createFixture(fixture_def);
            col_comp.getBody().setFixedRotation(true);
            col_comp.getBody().setUserData(entity);

            if (entity.equals(player)) {
                player_body = col_comp.getBody();
            }
        }
    }

    private void generate_tile_bodies() {
        for(int x = 0; x < C.MAP_WIDTH; x++) {
            for(int y = 0; y < C.MAP_HEIGHT; y++) {
                if(map.is_solid(x, y)) {
                    BodyDef body_def = new BodyDef();
                    body_def.position.set(x + 0.6f, y + 0.5f);

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(0.5f, 0.5f);

                    FixtureDef fixture_def = new FixtureDef();
                    fixture_def.shape = shape;

                    if(map.has_sight(x, y)) {
                        fixture_def.filter.categoryBits = C.BIT_WINDOW;
                    } else {
                        fixture_def.filter.categoryBits = C.BIT_WALL;
                    }

                    Body body = world.createBody(body_def);
                    body.createFixture(fixture_def);
                    body.setUserData(new Vector2(x, y));
                }
            }
        }
    }

    private void generate_door_bodies() {
        for(String door : map.getDoors()) {
            PositionComp posComp = mgr.comp(door, PositionComp.class);
            RotationComp rotComp = mgr.comp(door, RotationComp.class);
            RenderComp renderComp = mgr.comp(door, RenderComp.class);
            CollisionComp colComp = mgr.comp(door, CollisionComp.class);

            float w = renderComp.getW() * C.WTB;
            float h = renderComp.getH() * C.WTB;

            Body body = createBody(
                posComp.getX(), posComp.getY(),
                w, h, false, rotComp.getAng());

            colComp.setBody(body);
        }
    }

    private Body createBody(float x, float y, float w, float h, boolean sight, float ang) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w / 2, h / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        if (sight) {
            fixtureDef.filter.categoryBits = C.BIT_WINDOW;
        } else {
            fixtureDef.filter.categoryBits = C.BIT_WALL;
        }

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setTransform(x, y, (float)(ang * Math.PI / 180));

        shape.dispose();

        return body;
    }

    private void genderate_station_bodies() {

    }

    public void update() {
        Set<String> entities = mgr.entitiesWith(VelocityComp.class);

        for(String entity : entities) {
            PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
            VelocityComp vel_comp = mgr.comp(entity, VelocityComp.class);
            RotationComp rot_comp = mgr.comp(entity, RotationComp.class);
            CollisionComp col_comp = mgr.comp(entity, CollisionComp.class);

            pos_comp.setPrevX(pos_comp.getX());
            pos_comp.setPrevY(pos_comp.getY());

            if(vel_comp.getSpd() != 0) {
                Vector2 vel_vec = new Vector2(
                    vel_comp.getSpd() * rot_comp.getX(),
                    vel_comp.getSpd() * rot_comp.getY()
                );

                col_comp.getBody().applyLinearImpulse(
                    vel_vec, col_comp.getBody().getWorldCenter(), true
                );
            }

            rot_comp.setPrevAng(rot_comp.getAng());

            if(vel_comp.getAngSpd() != 0) {
                rot_comp.rotate(vel_comp.getAngSpd());
            }
        }

        world.step(C.BOX_STEP, C.BOX_VEL_ITER, C.BOX_POS_ITER);

        for(String entity : entities) {
            PositionComp pos_comp = mgr.comp(entity, PositionComp.class);
            CollisionComp col_comp = mgr.comp(entity, CollisionComp.class);

            pos_comp.setX(col_comp.getBody().getPosition().x);
            pos_comp.setY(col_comp.getBody().getPosition().y);
        }
    }

    public void render(SpriteBatch batch) {

    }

    public World get_world() {
        return world;
    }

    public Body getPlayerBody() {
        return player_body;
    }
}
