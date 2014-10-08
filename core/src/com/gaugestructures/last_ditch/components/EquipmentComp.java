package com.gaugestructures.last_ditch.components;

public class EquipmentComp extends GameComponent {
    private String lHead, rHead, lHand, rHand, lArm, rArm, torso, belt, lLeg, rLeg, lFoot, rFoot;

    public String getItemSlot(String item) {
        if (lHead != null && lHead.equals(item)) {
            return "lHead";
        } else if (rHead != null && rHead.equals(item)) {
            return "rHead";
        } else if (lHand != null && lHand.equals(item)) {
            return "lHand";
        } else if (rHand != null && rHand.equals(item)) {
            return "rHand";
        } else if (lArm != null && lArm.equals(item)) {
            return "lArm";
        } else if (rArm != null && rArm.equals(item)) {
            return "rArm";
        } else if (torso != null && torso.equals(item)) {
            return "torso";
        } else if (belt != null && belt.equals(item)) {
            return "belt";
        } else if (lLeg != null && lLeg.equals(item)) {
            return "lLeg";
        } else if (rLeg != null && rLeg.equals(item)) {
            return "rLeg";
        } else if (lFoot != null && lFoot.equals(item)) {
            return "lFoot";
        } else if (rFoot != null && rFoot.equals(item)) {
            return "rFoot";
        }

        return null;
    }

    public String getSlotByName(String slotName) {
        if (slotName.equals("lHead")) {
            return lHead;
        } else if (slotName.equals("rHead")) {
            return rHead;
        } else if (slotName.equals("lHand")) {
            return lHand;
        } else if (slotName.equals("rHand")) {
            return rHand;
        } else if (slotName.equals("lArm")) {
            return lArm;
        } else if (slotName.equals("rArm")) {
            return rArm;
        } else if (slotName.equals("torso")) {
            return torso;
        } else if (slotName.equals("belt")) {
            return belt;
        } else if (slotName.equals("lLeg")) {
            return lLeg;
        } else if (slotName.equals("rLeg")) {
            return rLeg;
        } else if (slotName.equals("lFoot")) {
            return lFoot;
        } else if (slotName.equals("rFoot")) {
            return rFoot;
        } else {
            return "";
        }
    }

    public void setSlot(String slot, String item) {
        if (slot.equals("lHead")) {
            lHead = item;
        } else if (slot.equals("rHead")) {
            rHead = item;
        } else if (slot.equals("lHand")) {
            lHand = item;
        } else if (slot.equals("rHand")) {
            rHand = item;
        } else if (slot.equals("lArm")) {
            lArm = item;
        } else if (slot.equals("rArm")) {
            rArm = item;
        } else if (slot.equals("torso")) {
            torso = item;
        } else if (slot.equals("belt")) {
            belt = item;
        } else if (slot.equals("lLeg")) {
            lLeg = item;
        } else if (slot.equals("rLeg")) {
            rLeg = item;
        } else if (slot.equals("lFoot")) {
            lFoot = item;
        } else if (slot.equals("rFoot")) {
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
