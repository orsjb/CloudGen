package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;

import java.util.List;

public class EveryoneDoingSomething implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        Number[] previousDoubles = DynamicSystemUtils.getOutputs(input.get(0)[0], Double.class);
        int numOutputs = previousDoubles.length;
        boolean[] changed = new boolean[numOutputs];
        for(int i = 0; i < numOutputs; i++) {
            changed[i] = false;
        }
        double totalCount = 0;
        for(Number[][] n : input) {
            previousDoubles = DynamicSystemUtils.getOutputs(n[0], Double.class);
            for(int i = 1; i < n.length; i++) {
                Number[] doubles = DynamicSystemUtils.getOutputs(n[i], Double.class);
                for(int j = 0; j < numOutputs; j++) {
                    if(!changed[j]) {
                        if(!previousDoubles[j].equals(doubles[j])) {
                            changed[j] = true;
                        }
                    }
                }
                previousDoubles = doubles;
            }
            double count = 0;
            for(int i = 0; i < numOutputs; i++) {
                if(changed[i]) {
                    count += 1/numOutputs;
                }
            }
            totalCount += count / input.size();
        }
        return new double[] {totalCount};
    }

    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
