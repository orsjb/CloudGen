package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

/**

 */
public class GrammarLength implements EvaluationMetric<Decider> {

    @Override
    public double[] getMetric(Decider d) {
        double score = d.getGenotypeString().length();
        return new double[] {score};
    }
    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
