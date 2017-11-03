package com.olliebown.Sandpit;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.grammar.Production;
import org.moeaframework.util.grammar.Rule;
import org.moeaframework.util.grammar.Symbol;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

public class MyMOEAGrammarTest {


    public class MyTestProblem extends AbstractProblem {


        public MyTestProblem() {
            super(1, 1);
        }

        @Override
        public void evaluate(Solution solution) {
            Grammar grammar = (Grammar) solution.getVariable(0);

            //explain how the CFG will be used to convert the grammar int array to the string
            ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar();

            //add the rules! Don't know what I'm doing here!
            Rule r = new Rule(new Symbol("x", false));
            Production p = new Production();
            p.add(new Symbol("x", false));
            r.add(new Production());
            contextFreeGrammar.add(r);
            //

            String grammarAsString = contextFreeGrammar.build(grammar.toArray());
//            Decider decider = Decider.constructFromStringGrammar(grammarAsString);


        }

        @Override
        public Solution newSolution() {
            Solution solution = new Solution(1, 1);
            solution.setVariable(0, new Grammar(10));
            return solution;
        }
    }

}
