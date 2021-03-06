package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.RealVariable;
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
            outputData = env.generateAllOutputDataSubset(d);
            double[] results = evaluate(outputData, d);
            for(int i = 0; i < numberOfObjectives; i++) {
                solution.setObjective(i, results[i]);
            }
        } else {
            for(int i = 0; i < numberOfObjectives; i++) {
                solution.setObjective(i, 10000000); //max out the solutions if we got a null D. We don't want it scoring points.
            }
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