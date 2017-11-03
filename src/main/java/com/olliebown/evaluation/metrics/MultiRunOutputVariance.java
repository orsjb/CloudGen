package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.evaluation.EvaluationMetric;

import java.util.List;

public class MultiRunOutputVariance implements EvaluationMetric<List<Number[][]>> {

    private AudioSimulationEnvironment env;

    public MultiRunOutputVariance(AudioSimulationEnvironment env) {
        this.env = env;
    }

    @Override
    public double[] getMetric(List<Number[][]> dsOutput) {
        double[] metric = null;

        //TODO

        return metric;
    }

    @Override
    public String[] getMetricInfo() {
        return null;
    }
}
