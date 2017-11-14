package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.problem.AbstractProblem;

import java.io.IOException;
import java.util.List;

public abstract class DeciderMOEAProblemNonGrammar extends AbstractProblem {

    AudioSimulationEnvironment env;

    public DeciderMOEAProblemNonGrammar(int numObjectives, String audioDir) throws IOException {
        super(1, numObjectives);
        env = new AudioSimulationEnvironment();
        env.loadAudioData(audioDir);
    }

    @Override
    public void evaluate(Solution solution) {
        if(solution == null || solution.getNumberOfVariables() == 0) {
            System.out.println("NONONONONONONON");
        }
        DeciderMOEAVariable var = (DeciderMOEAVariable)solution.getVariable(0);
        Decider d = null;
        if(var != null) {
            d = (var).getDecider();
        } else {
            System.out.println("VAR=NULL!!!");
        }
//        System.out.println("Evaluating: d="+d);
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
        Solution solution = new Solution(1, numberOfObjectives);
        DeciderMOEAVariable var = new DeciderMOEAVariable();
        solution.setVariable(0, var);
        return solution;
    }
}