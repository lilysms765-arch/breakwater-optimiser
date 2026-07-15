//Brain of the project,used to calculate the constraints

package com.breakwater.optimization;

import com.breakwater.model.BreakwaterDesign;
import com.breakwater.physics.HudsonCalculator;

public class Optimizer {

    private static final double CRANE_LIMIT = 25.0;

    public static BreakwaterDesign optimize(
            double waveHeight,
            double waterDepth) {

        BreakwaterDesign best = null;

        for (double slope = 1.0;
             slope <= 4.0;
             slope += 0.01) {

            double armorWeight =
                    HudsonCalculator.calculateArmorWeight(
                            waveHeight,
                            slope);

            boolean feasible =
                    armorWeight <= CRANE_LIMIT;

            double horizontalLength =
                    slope * waterDepth;

            double volume =
                    0.5 * waterDepth * horizontalLength;

            if (!feasible) {
                continue;
            }

            if (best == null ||
                    volume < best.getConcreteVolume()) {

                best = new BreakwaterDesign(
                        slope,
                        armorWeight,
                        volume,
                        true);
            }
        }

        return best;
    }
}
