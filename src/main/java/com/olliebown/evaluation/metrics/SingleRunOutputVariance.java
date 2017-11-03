package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;

import java.util.List;

public class SingleRunOutputVariance implements EvaluationMetric<List<Number[][]>> {

    private AudioSimulationEnvironment env;

    public SingleRunOutputVariance(AudioSimulationEnvironment env) {
        this.env = env;
    }

    @Override
    public double[] getMetric(List<Number[][]> dsOutput) {

        double variance = 0;
        for(Number[][] run : dsOutput) {

            //TODO

        }

        return new double[] {variance};
    }

    @Override
    public String[] getMetricInfo() {
        return new String[]{"SingleRunOutputVariance"};
    }
}
