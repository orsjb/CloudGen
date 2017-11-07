package com.olliebown.basic_decider_evolution;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.evaluation.metrics.MultiRunOutputVariance;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;

import java.util.List;

public class MaximiseVarianceFitnessFunction implements FitnessFunction {

    AudioSimulationEnvironment env;

    public MaximiseVarianceFitnessFunction() {
        env = new AudioSimulationEnvironment();
        env.loadAudioData("data/Redgate");
    }

    @Override
    public double evaluate(DynamicSystem ds) {
        ds.reset();
        List<Number[][]> results = env.generateAllOutputData(ds);
        //results now contains all of the system output from the multiple runs
        MultiRunOutputVariance evaluator = new MultiRunOutputVariance();
        double[] metric = evaluator.getMetric(results);
        double average = 0;
        for(int i = 0; i < metric.length; i++) {
            average += metric[i];
        }
        average /= metric.length;
        double fitness = average;
        return fitness;
    }

}
