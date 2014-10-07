package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.AIComp;
import com.gaugestructures.last_ditch.components.VelocityComp;

import java.util.Set;

public class AISystem extends GameSystem {
    private int tick = 0;

    private Manager mgr;

    public AISystem(Manager mgr) {
        this.mgr = mgr;
    }

    public void update() {
        if (tick < 80) {
            tick += 1;
        } else {
            tick = 0;

            Set<String> entities = mgr.entitiesWith(AIComp.class);

            for (String entity : entities) {
                AIComp aiComp = mgr.comp(entity, AIComp.class);

                if (aiComp.getType().equals("wander")) {
                    VelocityComp velComp = mgr.comp(entity, VelocityComp.class);

                    if (mgr.randInt(0, 10) > 0) {
                        float angSpd = mgr.randFloat(-velComp.getMaxAng(), velComp.getAngSpd());
                        velComp.setAngSpd(angSpd);
                        velComp.setSpd(velComp.getMaxSpd());
                    } else {
                        velComp.setSpd(0);
                        velComp.setAngSpd(0);
                    }
                }
            }
        }
    }
}
