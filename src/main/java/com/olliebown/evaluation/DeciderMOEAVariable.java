package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Variable;

import java.util.Random;

public class DeciderMOEAVariable implements Variable {

    static Random rng = new Random();
    Decider d;

    public DeciderMOEAVariable() {
        randomize();
    }

    @Override
    public Variable copy() {
        DeciderMOEAVariable newV = new DeciderMOEAVariable();
        newV.d = d;     //TODO - this is supposed to be a proper copy
        return newV;
    }

    @Override
    public void randomize() {
        d = Decider.newRandomTree(6, 26, rng, 0.05f);
    }

    public Decider getDecider() {
        return d;
    }
}
