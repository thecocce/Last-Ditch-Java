package com.gaugestructures.last_ditch.components;

public class ResourcesComp extends GameComponent {
    private float water = 0, energy = 0, fuel = 0;

    public float getAmount(String type) {
        if (type.equals("water")) {
            return water;
        } else if (type.equals("energy")) {
            return energy;
        } else if (type.equals("fuel")) {
            return fuel;
        } else {
            throw new RuntimeException("Invalid resource type.");
        }
    }

    public void changeAmount(String type, float amt) {
        if (type.equals("water")) {
            water += amt;
        } else if (type.equals("energy")) {
            energy += amt;
        } else if (type.equals("fuel")) {
            fuel += amt;
        } else {
            throw new RuntimeException("Invalid resource type.");
        }
    }

    public void setAmount(String type, float amt) {
        if (type.equals("water")) {
            water = amt;
        } else if (type.equals("energy")) {
            energy = amt;
        } else if (type.equals("fuel")) {
            fuel = amt;
        } else {
            throw new RuntimeException("Invalid resource type.");
        }
    }

    public float getWater() {
        return water;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }
}
