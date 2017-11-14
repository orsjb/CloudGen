package com.olliebown.executable.moea;

import com.olliebown.evaluation.DeciderMOEAProblemNonGrammar;
import com.olliebown.evaluation.DeciderMOEAVariable;
import com.olliebown.evaluation.DeciderMOEAVariation;
import com.olliebown.evaluation.metrics.DeciderSimulationStats;
import com.olliebown.evaluation.metrics.NumNodesVisited;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.*;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Proof of concept of the evolution. TWo targets. Look at the pareto front.
 */
public class MOEARunner10_NumLeavesVisitedAndEntropyNoGrammar {

    public static void main(String[] args) throws IOException {

        FileUtil util = new FileUtil();
        util.writeText("MOEARunner10_NumLeavesVisitedAndEntropyNoGrammar", "info.txt");

        DeciderMOEAProblemNonGrammar theProblem = new DeciderMOEAProblemNonGrammar(2, "data/Training_short") {

            int evalCount = 0;
            long lastTime = 0;

            @Override
            public double[] evaluate(List<Number[][]> outputData, Decider d) {
                double[] results = new double[numberOfObjectives];
                if(outputData != null) {
                    NumNodesVisited nl = new NumNodesVisited();
                    results[0] = -1 * nl.getMetric(outputData)[0];
                    DeciderSimulationStats dss = new DeciderSimulationStats();
                    double[] stats = dss.getMetric(outputData);
                    results[1] = -1 * stats[8];//entropy
                }
                lastTime = System.currentTimeMillis();
                return results;
            }
        };

//        Algorithm algorithmNSGAII = AlgorithmFactory.getInstance().getAlgorithm("NSGAII", properties, theProblem);

        TournamentSelection selection = new TournamentSelection(2, new ChainedComparator(
                new ParetoDominanceComparator(), new CrowdingComparator()));

        Variation variation = new DeciderMOEAVariation();

        Initialization initialization = new RandomInitialization( theProblem,
                100);

        Algorithm algorithmNSGAII = new NSGAII( theProblem,
                new NondominatedSortingPopulation(), null, // no archive
                selection,
                variation,
                initialization);

        FileWriter metrics = new FileWriter(new File(util.dir + "/metrics.csv"));
        metrics.write("Name,grammar,generation,num_leaves_visited,entropy\n");
        metrics.flush();
        int steps = 20000;
        int writeInterval = 1;
        for(int i = 0; i < steps; i++) {
            algorithmNSGAII.step();
            if (i % writeInterval == 0) {
                NondominatedPopulation population = algorithmNSGAII.getResult();
                for(int sol = 0; sol < population.size(); sol++) {
                    Solution s = population.get(sol);
                    Decider d = ((DeciderMOEAVariable)s.getVariable(0)).getDecider();
                    String name = "gen_" + i + "_#" + sol;
                    util.write(d,name);
                    metrics.write(name + "," + d.getGenotypeString().replace(",", "?") + "," + i);
                    for(int objective = 0; objective < s.getNumberOfObjectives(); objective++) {
                        metrics.write("," + s.getObjective(objective));
                    }
                    metrics.write("\n");
                    metrics.flush();

                }
                System.out.println("Step " + i);
            }
        }

        metrics.close();

    }

}
