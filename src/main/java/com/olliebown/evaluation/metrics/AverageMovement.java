package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;

import java.util.List;

public class AverageMovement implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        double avg = 0;
        int count = 0;
        for(Number[][] run : input) {
            for(int i = 1; i < run.length; i++) {
                Number[] lastFilteredRun = DynamicSystemUtils.getOutputs(run[i-1], Double.class);
                Number[] filteredRun = DynamicSystemUtils.getOutputs(run[i], Double.class);
                for (int j = 0; j < filteredRun.length; j++) {
                    avg += Math.abs(filteredRun[j].doubleValue() - lastFilteredRun[j].doubleValue());
                    count++;
                }
            }
        }
        return new double[]{avg / count};
    }

    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
