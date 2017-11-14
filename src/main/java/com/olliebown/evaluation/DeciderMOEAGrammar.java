package com.olliebown.evaluation;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.grammar.Parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

public abstract class DeciderMOEAGrammar {

    static ContextFreeGrammar grammar;
    static Random rng = new Random();

    static {
        try {
            grammar = Parser.load(new StringReader(Decider.GRAMMAR));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Decider generateDecider(Solution s) {
        int[] codon = ((Grammar)s.getVariable(0)).toArray();
        String grammarFunction = grammar.build(codon);
        //now build the decider from the grammar function
        if(grammarFunction == null) {
            return null;
        }
        Decider d = Decider.parseFromString(6, 26, grammarFunction, rng);
        return d;
    }

}