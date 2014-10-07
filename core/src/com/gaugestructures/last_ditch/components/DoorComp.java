package com.gaugestructures.last_ditch.components;

public class DoorComp extends GameComponent {
    private boolean open = false, locked = false;

    public boolean isOpen() {
        return open;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
