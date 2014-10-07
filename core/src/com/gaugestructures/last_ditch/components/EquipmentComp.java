package com.gaugestructures.last_ditch.components;

public class EquipmentComp extends GameComponent {
    private String lHead, rHead, lHand, rHand, lArm, rArm, torso, belt, lLeg, rLeg, lFoot, rFoot;

    public String getSlot(String slot) {
        if(slot.equals("lHead")) {
            return lHead;
        } else if(slot.equals("rHead")) {
            return rHead;
        } else if(slot.equals("lHand")) {
            return lHand;
        } else if(slot.equals("rHand")) {
            return rHand;
        } else if(slot.equals("lArm")) {
            return lArm;
        } else if(slot.equals("rArm")) {
            return rArm;
        } else if(slot.equals("torso")) {
            return torso;
        } else if(slot.equals("belt")) {
            return belt;
        } else if(slot.equals("lLeg")) {
            return lLeg;
        } else if(slot.equals("rLeg")) {
            return rLeg;
        } else if(slot.equals("lFoot")) {
            return lFoot;
        } else if(slot.equals("rFoot")) {
            return rFoot;
        } else {
            return "";
        }
    }

    public void setSlot(String slot, String item) {
        if(slot.equals("lHead")) {
            lHead = item;
        } else if(slot.equals("rHead")) {
            rHead = item;
        } else if(slot.equals("lHand")) {
            lHand = item;
        } else if(slot.equals("rHand")) {
            rHand = item;
        } else if(slot.equals("lArm")) {
            lArm = item;
        } else if(slot.equals("rArm")) {
            rArm = item;
        } else if(slot.equals("torso")) {
            torso = item;
        } else if(slot.equals("belt")) {
            belt = item;
        } else if(slot.equals("lLeg")) {
            lLeg = item;
        } else if(slot.equals("rLeg")) {
            rLeg = item;
        } else if(slot.equals("lFoot")) {
            lFoot = item;
        } else if(slot.equals("rFoot")) {
            rFoot = item;
        }
    }

    public String getLHead() {
        return lHead;
    }

    public String getRHead() {
        return rHead;
    }

    public String getLHand() {
        return lHand;
    }

    public String getRHand() {
        return rHand;
    }

    public String getLArm() {
        return lArm;
    }

    public String getRArm() {
        return rArm;
    }

    public String getTorso() {
        return torso;
    }

    public String getBelt() {
        return belt;
    }

    public String getLLeg() {
        return lLeg;
    }

    public String getRLeg() {
        return rLeg;
    }

    public String getLFoot() {
        return lFoot;
    }

    public String getRFoot() {
        return rFoot;
    }
}
