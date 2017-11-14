package com.olliebown.executable.moea;

import com.olliebown.evaluation.DeciderMOEAGrammar;
import com.olliebown.evaluation.DeciderMOEAProblem;
import com.olliebown.evaluation.metrics.DeciderSimulationStats;
import com.olliebown.evaluation.metrics.NumLeaves;
import com.olliebown.evaluation.metrics.NumNodesVisited;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Proof of concept of the evolution. TWo targets. Look at the pareto front.
 */
public class MOEARunner7_JustLeavesTouched {

    public static void main(String[] args) throws IOException {

        FileUtil util = new FileUtil();
        util.writeText("MOEARunner7_JustLeavesTouched", "info.txt");

        DeciderMOEAProblem theProblem = new DeciderMOEAProblem(1, "data/Training_short") {

            int evalCount = 0;
            long lastTime = 0;

            @Override
            public double[] evaluate(List<Number[][]> outputData, Decider d) {
                double[] results = new double[numberOfObjectives];
                if(outputData != null) {
                    NumNodesVisited nnv = new NumNodesVisited();
                    results[0] = -1 * nnv.getMetric(outputData)[0];
                }
                lastTime = System.currentTimeMillis();
                return results;
            }
        };
        Properties properties = new Properties();
        properties.setProperty("populationSize", "1000");

        Algorithm algorithmNSGAII = AlgorithmFactory.getInstance().getAlgorithm(
                "NSGAII", properties, theProblem);
        FileWriter metrics = new FileWriter(new File(util.dir + "/metrics.csv"));
        metrics.write("Name,grammar,generation,leaves_touched\n");
        metrics.flush();
        int steps = 20000;
        int writeInterval = 10;
        for(int i = 0; i < steps; i++) {
            algorithmNSGAII.step();
            if (i % writeInterval == 0) {
                NondominatedPopulation population = algorithmNSGAII.getResult();
                for(int sol = 0; sol < population.size(); sol++) {
                    Solution s = population.get(sol);
                    Decider d = DeciderMOEAGrammar.generateDecider(s);
                    String name = "gen_" + i + "_#" + sol;
                    util.write(d,name);
                    metrics.write(name + "," + d.getGenotypeString().replace(",", ":") + "," + i);
                    for(int objective = 0; objective < s.getNumberOfObjectives(); objective++) {
                        metrics.write("," + s.getObjective(objective));
                    }
                    metrics.write("\n");
                    metrics.flush();

                }
                System.out.println("Step " + i + " " + population.get(0).getObjective(0));
            }
        }

        metrics.close();

    }

}
