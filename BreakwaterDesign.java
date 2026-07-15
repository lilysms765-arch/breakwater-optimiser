//This is used to take data from Hudson Formula and store them
package com.breakwater.model;

public class BreakwaterDesign {

    private double slope;
    private double armorWeight;
    private double concreteVolume;
    private boolean craneFeasible;

    public BreakwaterDesign(
            double slope,
            double armorWeight,
            double concreteVolume,
            boolean craneFeasible) {

        this.slope = slope;
        this.armorWeight = armorWeight;
        this.concreteVolume = concreteVolume;
        this.craneFeasible = craneFeasible;
    }

    public double getSlope() {
        return slope;
    }

    public double getArmorWeight() {
        return armorWeight;
    }

    public double getConcreteVolume() {
        return concreteVolume;
    }

    public boolean isCraneFeasible() {
        return craneFeasible;
    }
}
