package com.olliebown.sandpit;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
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
                            "<expr> ::= (<decision>)\n"
                                    + "<node> ::= (<decision>)|(<leaf>)\n"
                                    + "<decision> ::= D <index>,<thresh>,<node>,<node>\n"
                                    + "<index> ::= '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'10'|'11'|'12'|'13'|'14'|'15'|'16'|'17'|'18'|'19'\n"
                                    + "<thresh> ::= '0.0'|'0.1'|'0.2'|'0.3'|'0.3'|'0.4'|'0.5'|'0.6'|'0.7'|'0.8'|'0.9'|'1.0'\n"
                                    + "<leaf> ::= <index>,<op>,<index>|<leaf>,<leaf>\n"
                                    + "<op> ::= '*'|'+'|'-'\n"));
        }

        @Override
        public void evaluate(Solution solution) {
            int[] codon = ((Grammar)solution.getVariable(0)).toArray();
            String grammarFunction = grammar.build(codon);
            //now build the decider from the grammar function
            Decider d = Decider.parseFromString(6, 20, grammarFunction, new Random());

            //TODO measure the Decider!

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
                        "<expr> ::= (<decision>)\n"
                                + "<node> ::= (<decision>)|(<leaf>)\n"
                                + "<decision> ::= D <index>,<thresh>,<node>,<node>\n"
                                + "<index> ::= '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'10'|'11'|'12'|'13'|'14'|'15'|'16'|'17'|'18'|'19'\n"
                                + "<thresh> ::= '0.0'|'0.1'|'0.2'|'0.3'|'0.3'|'0.4'|'0.5'|'0.6'|'0.7'|'0.8'|'0.9'|'1.0'\n"
                                + "<leaf> ::= <index>,<op>,<index>|<leaf>,<leaf>\n"
                                + "<op> ::= '*'|'+'|'-'\n"));

        Grammar g = new Grammar(100);
        g.randomize();
        int[] codon = g.toArray();
        if(grammar != null) {
            String s = grammar.build(codon);
            System.out.println(s);
        } else {
            System.out.println("Grammar = null");
        }
    }


}
