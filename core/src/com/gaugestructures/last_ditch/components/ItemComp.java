package com.gaugestructures.last_ditch.components;

public class ItemComp extends GameComponent {
    private boolean usable = false, equipped = false;
    private float value;
    private float quality = 0.5f, condition = 1f, weight = 0.5f;
    private float baseValue = 1f, decayRate = 0.01f;

    public ItemComp() {}

    public ItemComp(float quality, float condition) {
        this.quality = quality;
        this.condition = condition;

        value = baseValue * (2 * quality + 1 * condition);
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public float getQuality() {
        return quality;
    }

    public float getCondition() {
        return condition;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
        value = baseValue * (2 * quality + 1 * condition);
    }

    public float getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(float decayRate) {
        this.decayRate = decayRate;
    }

    public float getValue() {
        return value;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    public void setCondition(float condition) {
        this.condition = condition;
        value = baseValue * (2 * quality + 1 * condition);
    }

    public void setQuality(float quality) {
        this.quality = quality;
        value = baseValue * (2 * quality + 1 * condition);
    }
}
