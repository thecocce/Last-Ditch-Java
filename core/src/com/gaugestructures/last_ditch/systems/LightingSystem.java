package com.gaugestructures.last_ditch.systems;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gaugestructures.last_ditch.C;

public class LightingSystem extends GameSystem {
    private OrthographicCamera cam;
    private PhysicsSystem physics;
    private RayHandler handler;
    private PointLight central_light;

    public LightingSystem(OrthographicCamera cam, PhysicsSystem physics) {
        this.cam = cam;
        this.physics = physics;

        handler = new RayHandler(physics.get_world());

        RayHandler.isDiffuse = true;
        Light.setContactFilter(C.BIT_LIGHT, C.BIT_ZERO, C.BIT_WALL);

        central_light = new PointLight(handler, 600);
        central_light.setSoft(true);
        central_light.setSoftnessLength(1.2f);
        central_light.setColor(0.8f, 0.8f, 0.8f, 1f);
        central_light.setDistance(1000f);
        central_light.attachToBody(physics.get_player_body(), 0, 0);
    }

    public void render() {
        handler.setCombinedMatrix(cam.combined.scl(C.BTW));
        handler.updateAndRender();
    }
}
