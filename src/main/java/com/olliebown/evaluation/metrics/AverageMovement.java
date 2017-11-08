package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;

import java.util.List;

public class AverageMovement implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        double avg = 0;
        int count = 0;
        for(Number[][] run : input) {
            for(int i = 0; i < run.length; i++) {
                for (int j = 1; j < run[i].length; j++) {
                    avg += Math.abs(run[i][j].doubleValue() - run[i][j-1].doubleValue());
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
