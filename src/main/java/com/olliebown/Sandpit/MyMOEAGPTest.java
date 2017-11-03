package com.olliebown.Sandpit;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.grammar.Production;
import org.moeaframework.util.grammar.Rule;
import org.moeaframework.util.grammar.Symbol;
import org.moeaframework.util.tree.IfElse;
import org.moeaframework.util.tree.Rules;

public class MyMOEAGPTest {


    public class MyTestProblem extends AbstractProblem {

        Rules theRules;

        public MyTestProblem() {

            super(1, 1);
            theRules = new Rules();
            theRules.add(new IfElse());
        }

        @Override
        public void evaluate(Solution solution) {



        }

        @Override
        public Solution newSolution() {
            Solution solution = new Solution(1, 1);
            solution.setVariable(0, new Program(theRules));
            return solution;
        }
    }

}
