package com.gaugestructures.last_ditch.components;

public class NeedsComp extends GameComponent {
    private float hunger = 1f, thirst = 1f, sanity = 1f;
    private float hungerRate = -0.000027f, thirstRate = -0.000084f, sanityRate = -0.0000069f;
    private float energy = 1f, energyMax = 1f, energyFatigueRate = -0.000193f;
    private float energyRecoverRate = 0.01f, energyUsageRate = -0.005f;

    public void modHunger(float amt) {
        hunger += hungerRate * amt;

        if (hunger < 0) {
            hunger = 0;
        }
    }

    public void modThirst(float amt) {
        thirst += thirstRate * amt;

        if (thirst < 0) {
            thirst = 0;
        }
    }

    public void modEnergy(float amt, boolean recovery) {
        if (recovery) {
            energy += energyRecoverRate * amt;

            if (energy > energyMax) {
                energy = energyMax;
            }
        } else {
            energy += energyUsageRate * amt;

            if (energy < 0) {
                energy = 0;
            }
        }
    }

    public void fatigueEnergy(float amt) {
        energyMax += energyFatigueRate * amt;

        if (energyMax < 0) {
            energyMax = 0;
        }
    }

    public void modSanity(float amt) {
        sanity += sanityRate * amt;

        if (sanity < 0) {
            sanity = 0;
        }
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = hunger;
    }

    public float getThirst() {
        return thirst;
    }

    public void setThirst(float thirst) {
        this.thirst = thirst;
    }

    public float getSanity() {
        return sanity;
    }

    public void setSanity(float sanity) {
        this.sanity = sanity;
    }

    public float getHungerRate() {
        return hungerRate;
    }

    public void setHungerRate(float hungerRate) {
        this.hungerRate = hungerRate;
    }

    public float getThirstRate() {
        return thirstRate;
    }

    public void setThirstRate(float thirstRate) {
        this.thirstRate = thirstRate;
    }

    public float getSanityRate() {
        return sanityRate;
    }

    public void setSanityRate(float sanityRate) {
        this.sanityRate = sanityRate;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getEnergyMax() {
        return energyMax;
    }

    public void setEnergyMax(float energyMax) {
        this.energyMax = energyMax;
    }

    public float getEnergyFatigueRate() {
        return energyFatigueRate;
    }

    public void setEnergyFatigueRate(float energyFatigueRate) {
        this.energyFatigueRate = energyFatigueRate;
    }

    public float getEnergyRecoverRate() {
        return energyRecoverRate;
    }

    public void setEnergyRecoverRate(float energyRecoverRate) {
        this.energyRecoverRate = energyRecoverRate;
    }

    public float getEnergyUsageRate() {
        return energyUsageRate;
    }

    public void setEnergyUsageRate(float energyUsageRate) {
        this.energyUsageRate = energyUsageRate;
    }
}
