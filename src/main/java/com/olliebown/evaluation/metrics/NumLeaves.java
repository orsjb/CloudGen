package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.util.ArrayList;
import java.util.List;

/**

 */
public class NumLeaves implements EvaluationMetric<Decider> {

    @Override
    public double[] getMetric(Decider d) {
        double score = d.getNumLeaves();
        return new double[] {score};
    }
    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
