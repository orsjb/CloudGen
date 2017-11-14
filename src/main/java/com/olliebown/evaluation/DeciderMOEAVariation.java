package com.olliebown.evaluation;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class DeciderMOEAVariation implements Variation {

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public Solution[] evolve(Solution[] solutions) {
        DeciderMOEAVariable var = (DeciderMOEAVariable) solutions[0].getVariable(0);
        DeciderMOEAVariable copy = new DeciderMOEAVariable();
        copy.d = var.d.copyMutate();
        Solution s = new Solution(solutions[0].getNumberOfVariables(), solutions[0].getNumberOfObjectives());
        s.setVariable(0, copy);
        return new Solution[]{s};
    }
}
