package com.olliebown.evaluation;

public interface EvaluationMetric<T> {

    public double[] getMetric(T input);
    public String[] getMetricInfo();

}
