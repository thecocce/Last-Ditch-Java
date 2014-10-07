package com.gaugestructures.last_ditch.components;

import com.badlogic.gdx.physics.box2d.Body;

public class CollisionComp extends GameComponent {
    private Body body;

    public CollisionComp() {}

    public CollisionComp(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
