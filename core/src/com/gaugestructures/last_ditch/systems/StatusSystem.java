package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.Manager;
import com.gaugestructures.last_ditch.components.NeedsComp;
import com.gaugestructures.last_ditch.components.VelocityComp;

public class StatusSystem extends GameSystem {
    private Manager mgr;
    private TimeSystem time;

    public StatusSystem(Manager mgr) {
        this.mgr = mgr;
        this.time = mgr.getTime();
    }

    public void update() {
        if (!mgr.isPaused()) {
            VelocityComp velComp = mgr.comp(mgr.getPlayer(), VelocityComp.class);
            NeedsComp needsComp = mgr.comp(mgr.getPlayer(), NeedsComp.class);

            float gd = time.getGameDelta();

            needsComp.fatigueEnergy(gd);

            needsComp.modHunger(gd);
            needsComp.modThirst(gd);

            if (velComp.getSpd() > 0) {
                needsComp.modEnergy(gd, false);
            } else if (velComp.getSpd() < 0) {
                needsComp.modEnergy(gd * 0.4f, false);
            } else {
                needsComp.modEnergy(gd, true);
            }

            needsComp.modSanity(gd);
        }
    }
}
