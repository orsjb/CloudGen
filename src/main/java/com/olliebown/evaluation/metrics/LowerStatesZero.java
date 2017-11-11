package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;

import java.util.List;

public class LowerStatesZero implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        double avgClosenessToZero = 0;
        int count = 0;
        for(Number[][] run : input) {
            for(int i = 0; i < run.length; i++) {
                for (int j = 0; j < run[i].length / 2; j++) {
                    avgClosenessToZero += 1 - run[i][j].doubleValue();
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
