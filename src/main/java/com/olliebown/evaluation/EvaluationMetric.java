package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;

public interface EvaluationMetric {

    public double[] getMetric(DynamicSystem ds);
    public String[] getMetricInfo();

}
