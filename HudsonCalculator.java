//Main physics or formula behind our project
package com.breakwater.physics;

public class HudsonCalculator {

    private static final double KD = 3.5;  // Standard value for concrete armor units
    private static final double SR = 2.4;  // Specific gravity of concrete
    private static final double WR = 2.4;  // Unit weight of concrete (t/m³)

    public static double calculateArmorWeight(
            double waveHeight,
            double slope) {

        // Hudson Formula: W = (WR × H³) / (KD × (SR-1)³ × cot θ)
        double numerator = WR * Math.pow(waveHeight, 3);

        double denominator =
                KD * Math.pow(SR - 1, 3) * slope;

        return numerator / denominator;
    }
}
