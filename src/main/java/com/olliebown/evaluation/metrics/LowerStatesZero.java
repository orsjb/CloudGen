package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;

import java.util.List;

public class LowerStatesZero implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        double avgClosenessToZero = 0;
        int count = 0;
        for(Number[][] run : input) {
            for(int i = 0; i < run.length; i++) {
                Number[] filteredRun = DynamicSystemUtils.getOutputs(run[i], Double.class);
                for (int j = 0; j < filteredRun.length / 2; j++) {
                    avgClosenessToZero += 1 - filteredRun[j].doubleValue();
                    count++;
                }
            }
        }
        return new double[]{avgClosenessToZero / count};
    }

    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
