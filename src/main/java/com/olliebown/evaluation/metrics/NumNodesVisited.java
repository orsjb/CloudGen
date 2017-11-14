package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NumNodesVisited implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        Set<Integer> nodesVisited = new HashSet<>();
        for(Number[][] run : input) {
            for(int i = 0; i < run.length; i++) {
                Number[] output = DynamicSystemUtils.getOutputs(run[i], Integer.class);
                Integer state = output[0].intValue();
                nodesVisited.add(state);
            }
        }
        return new double[]{nodesVisited.size()};
    }

    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
