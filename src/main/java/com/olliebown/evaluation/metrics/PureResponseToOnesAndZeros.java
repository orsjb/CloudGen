package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;

/**
 * Feeds simple inputs of 1s and 0s to the system. The 1s should produce a different output from the 0s.
 * The result is the difference in centroid between the 1s and 0s outputs after a run.
 */
public class PureResponseToOnesAndZeros implements EvaluationMetric<DynamicSystem> {

    Number[] zerosInput = new Number[6];
    Number[] onesInput = new Number[6];

    public PureResponseToOnesAndZeros() {
        for(int i = 0; i < 6; i++) {
            zerosInput[i] = 0;
            onesInput[i] = 1;
        }
    }

    @Override
    public double[] getMetric(DynamicSystem dynamicSystem) {
        double result = 0;
        int steps = 100;
        double[] zerosCentroid = new double[DynamicSystemUtils.getOutputs(dynamicSystem.getOutputs(), Double.class).length];
        double[] onesCentroid = new double[DynamicSystemUtils.getOutputs(dynamicSystem.getOutputs(), Double.class).length];
        //the zeroes
        dynamicSystem.reset();
        for(int i = 0; i < steps; i++) {
            dynamicSystem.update(zerosInput);
            Number[] outputs = DynamicSystemUtils.getOutputs(dynamicSystem.getOutputs(), Double.class);
            for(int j = 0; j < zerosCentroid.length; j++) {
                zerosCentroid[j] += outputs[j].doubleValue() / steps;
            }
        }
        //the ones
        dynamicSystem.reset();
        for(int i = 0; i < steps; i++) {
            dynamicSystem.update(onesInput);
            Number[] outputs = DynamicSystemUtils.getOutputs(dynamicSystem.getOutputs(), Double.class);
            for(int j = 0; j < onesCentroid.length; j++) {
                onesCentroid[j] += outputs[j].doubleValue() / steps;
            }
        }
        result = DynamicSystemUtils.distance(zerosCentroid, onesCentroid);
        return new double[]{result};
    }

    @Override
    public String[] getMetricInfo() {
        return new String[] {"Single double valued output. Feeds simple inputs of 1s and 0s to the system. The 1s should produce a different output from the 0s."};
    }
}
