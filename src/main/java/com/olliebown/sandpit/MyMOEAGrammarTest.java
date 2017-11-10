package com.olliebown.sandpit;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import net.happybrackets.patternspace.dynamic_system.decider.Operation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.grammar.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

public class MyMOEAGrammarTest {


    public class MyTestProblem extends AbstractProblem {

        ContextFreeGrammar grammar;

        public MyTestProblem() throws IOException {
            super(1, 1);
            grammar = Parser
                    .load(new StringReader(
                            Decider.GRAMMAR));
        }

        @Override
        public void evaluate(Solution solution) {
            int[] codon = ((Grammar)solution.getVariable(0)).toArray();
            String grammarFunction = grammar.build(codon);
            //now build the decider from the grammar function
            Decider d = Decider.parseFromString(6, 20, grammarFunction, new Random());

            //TODO fitness function goes here

            double result = 0;
            solution.setObjective(0, result);

        }

        @Override
        public Solution newSolution() {
            Solution solution = new Solution(1, 1);
            solution.setVariable(0, new Grammar(10));
            return solution;
        }
    }

    public static void main(String[] args) throws Exception {
        ContextFreeGrammar grammar = Parser
                .load(new StringReader(
                        Decider.GRAMMAR));

        Grammar g = new Grammar(1000);
        g.randomize();
        int[] codon = g.toArray();
        String s = grammar.build(codon);
        if(s != null) {
            System.out.println(s);          //why does this sometimes return null?
            Decider d = Decider.parseFromString(6, 20, s, new Random());
            Number[] input = new Number[6];
            for(int i = 0; i < input.length; i++) {
                input[i] = 0;
            }
            d.randomiseState();
            for(int i = 0; i < 1000; i++) {
                d.update(input);
                Number[] output = d.getOutputs();
                for(Number n : output) {
                    System.out.print(n + " ");
                }
                System.out.println();

            }
        }


    }


}
