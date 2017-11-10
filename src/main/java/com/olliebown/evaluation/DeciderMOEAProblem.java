package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.problem.AbstractProblem;

import java.io.IOException;
import java.util.List;

public abstract class DeciderMOEAProblem extends AbstractProblem {

    public DeciderMOEAProblem() throws IOException {
        super(1, 1);
    }

    @Override
    public void evaluate(Solution solution) {
        Decider d = DeciderMOEAGrammar.generateDecider(solution);
        List<Number[][]> outputData = null;
        if(d != null) {
            AudioSimulationEnvironment env = new AudioSimulationEnvironment();
            outputData = env.generateAllOutputData(d);
        }
        double[] results = evalute(outputData);
        for(int i = 0; i < results.length; i++) {
            solution.setObjective(i, results[i]);
        }
    }

    public abstract double[] evalute(List<Number[][]> outputData);

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(1, 1);
        solution.setVariable(0, new Grammar(10));
        return solution;
    }
}