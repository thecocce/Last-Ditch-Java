package com.gaugestructures.last_ditch.components;

public class ItemComp extends Component {
    private boolean usable = false;
    private float quality = 0.5f, condition = 1f, weight = 0.5f;
    private float base_value = 1f, decay_rate = 0.01f;
    private float value = base_value * (2 * quality + 1 * condition);

    public ItemComp() {}

    public ItemComp(float quality, float condition) {
        this.quality = quality;
        this.condition = condition;
    }

    public boolean is_usable() {
        return usable;
    }

    public void set_usable(boolean usable) {
        this.usable = usable;
    }

    public float get_quality() {
        return quality;
    }

    public float get_condition() {
        return condition;
    }

    public float get_weight() {
        return weight;
    }

    public void set_weight(float weight) {
        this.weight = weight;
    }

    public float get_base_value() {
        return base_value;
    }

    public void set_base_value(float base_value) {
        this.base_value = base_value;
        value = base_value * (2 * quality + 1 * condition);
    }

    public float get_decay_rate() {
        return decay_rate;
    }

    public void set_decay_rate(float decay_rate) {
        this.decay_rate = decay_rate;
    }

    public float get_value() {
        return value;
    }

    public void set_condition(float condition) {
        this.condition = condition;
        value = base_value * (2 * quality + 1 * condition);
    }

    public void set_quality(float quality) {
        this.quality = quality;
        value = base_value * (2 * quality + 1 * condition);
    }
}
