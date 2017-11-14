package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Allocates a specific node, index 19, as the rhythm node and tests to see if it produces a steady rhythm in the short term
 * and a changing tempo in the long term.
 */
public class SteadyRhythms implements EvaluationMetric<List<Number[][]>> {

    @Override
    public double[] getMetric(List<Number[][]> input) {
        double score = 0;
        double previousVal = 0;
        int previousTime = 0;
        List<Integer> intervals = new ArrayList<>();
        for(Number[][] n : input) {
            previousVal = 0;
            previousTime = 0;
            for(int i = 0; i < n.length; i++) {
                double val = n[i][19].doubleValue();
                if(val < previousVal) {
                    intervals.add(i - previousTime);
                    previousTime = i;
                }
                previousVal = val;
            }
        }
        //now intervals is populated with all of the cycle intervals
        //so we want to give max score to system that has consistent intervals (difference between one interval and next is low)
        //and has sme variation -- take a running average over the intervals and score variation more highly
        List<Double> runningAverage = new ArrayList<>();
        for(int i = 5; i < intervals.size(); i++) {
            double avg = 0;
            for(int j = 0; j < 5; j++) {
                avg += intervals.get(i - j);
            }
            avg /= 5;
            int diff = Math.abs(intervals.get(i) - intervals.get(i-1));
            score -= diff;
        }
        score /= (intervals.size() - 5);
        if(intervals.size() < 7) {
            score -= 1000;
        }
        return new double[] {score};
    }
    @Override
    public String[] getMetricInfo() {
        return new String[0];
    }
}
