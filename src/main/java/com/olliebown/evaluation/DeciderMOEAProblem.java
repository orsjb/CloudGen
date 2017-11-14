package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.problem.AbstractProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class DeciderMOEAProblem extends AbstractProblem {

    AudioSimulationEnvironment env;

    public DeciderMOEAProblem(int numObjectives, String audioDir) throws IOException {
        super(1, numObjectives);
        env = new AudioSimulationEnvironment();
        env.loadAudioData(audioDir);
    }

    @Override
    public void evaluate(Solution solution) {
        Decider d = DeciderMOEAGrammar.generateDecider(solution);
        List<Number[][]> outputData = null;
        if(d != null) {
            outputData = env.generateAllOutputData(d);
        }
        //create a subset permutation of the outputData.
        LinkedList<Number[][]> outputDataSubset = new LinkedList<>();
        for(Number[][] n : outputData) {
            outputDataSubset.add(n);
        }
        //randomly remove half the data from the subset
        for(int i = 0; i < outputData.size() / 2; i++) {
            int random = (int)(Math.random() * outputData.size());
            outputDataSubset.remove(random);
        }
        double[] results = evaluate(outputDataSubset, d);
        for(int i = 0; i < results.length; i++) {
            solution.setObjective(i, results[i]);
        }
    }

    public abstract double[] evaluate(List<Number[][]> outputData, Decider d);

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, numberOfObjectives);       //one grammar and 50 threshold values
        solution.setVariable(0, new Grammar(100));
        return solution;
    }
}