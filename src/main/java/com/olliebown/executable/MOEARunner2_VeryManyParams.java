package com.olliebown.executable;

import com.olliebown.evaluation.DeciderMOEAGrammar;
import com.olliebown.evaluation.DeciderMOEAProblem;
import com.olliebown.evaluation.metrics.*;
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
 * Proof of concept of the evolution. We are just trying to maximise amount of movement of the outputs
 */
public class MOEARunner2_VeryManyParams {

    public static void main(String[] args) throws IOException {

        FileUtil util = new FileUtil();
        util.writeText("MOEARunner2_VeryManyParams", "info.txt");

        DeciderMOEAProblem theProblem = new DeciderMOEAProblem(7, "data/Training") {

            int evalCount = 0;
            long lastTime = 0;

            @Override
            public double[] evaluate(List<Number[][]> outputData, Decider d) {
                double[] results = new double[numberOfObjectives];
                if(outputData != null) {
                    AverageMovement metric1 = new AverageMovement();
                    results[0] = -1 * metric1.getMetric(outputData)[0];
                    PureResponseToOnesAndZeros metric2 = new PureResponseToOnesAndZeros();
                    results[1] = -1 * metric2.getMetric(d)[0];
                    MultiRunOutputVariance metric3 = new MultiRunOutputVariance();
                    results[2] = -1 * metric3.getMetric(outputData)[0];
                    SteadyRhythms metric4 = new SteadyRhythms();
                    results[3] = -1 * metric4.getMetric(outputData)[0];
                    DeciderSimulationStats metric5 = new DeciderSimulationStats();
                    double[] stats = metric5.getMetric(outputData);
                    results[4] = -1 * stats[8]; //entropy
                    results[5] = -1 * stats[1]; //number of nodes visited
                    EveryoneDoingSomething metric6 = new EveryoneDoingSomething();
                    results[6] = -1 * metric6.getMetric(outputData)[0];
                }
                System.out.print("  - Evaluation result: " + evalCount++ + " -- ");
                for(int i = 0; i < results.length; i++) {
                    System.out.print(results[i] + " ");
                }
                System.out.println("Time = " + (System.currentTimeMillis() - lastTime));
                lastTime = System.currentTimeMillis();
                return results;
            }
        };
        Properties properties = new Properties();
        properties.setProperty("populationSize", "100");
        Algorithm algorithmNSGAII = AlgorithmFactory.getInstance().getAlgorithm(
                "NSGAII", properties, theProblem);
        Algorithm algorithmSPEA2 = AlgorithmFactory.getInstance().getAlgorithm(
                "SPEA2", properties, theProblem);
        FileWriter metrics = new FileWriter(new File(util.dir + "/metrics.csv"));
        metrics.write("Name,generation,avg_mvmnt,pure_response,variance,steady,entropy,total_nodes,everyone_busy\n");
        int steps = 20000;
        int writeInterval = 10;
        for(int i = 0; i < steps; i++) {
            algorithmNSGAII.step();
            algorithmSPEA2.step();
            if (i % writeInterval == 0) {

                NondominatedPopulation population = algorithmNSGAII.getResult();
                for(int sol = 0; sol < population.size(); sol++) {
                    Solution s = population.get(sol);
                    Decider d = DeciderMOEAGrammar.generateDecider(s);
                    String name = "gen_NSGAII_" + i + "_#" + sol;
                    util.write(d,name);
                    metrics.write(name);
                    for(int objective = 0; objective < s.getNumberOfObjectives(); objective++) {
                        metrics.write("," + s.getObjective(objective));
                    }
                    metrics.write("\n");

                }
                System.out.println("Step NSGAII " + i + ", objective1=" + population.get(0).getObjective(0)
                        + ", objective2=" + population.get(0).getObjective(1));

                population = algorithmSPEA2.getResult();
                for(int sol = 0; sol < population.size(); sol++) {
                    Solution s = population.get(sol);
                    Decider d = DeciderMOEAGrammar.generateDecider(s);
                    String name = "gen_SPEA2_" + i + "_#" + sol;
                    util.write(d,name);
                    metrics.write(name);
                    metrics.write(i);
                    for(int objective = 0; objective < s.getNumberOfObjectives(); objective++) {
                        metrics.write("," + s.getObjective(objective));
                    }
                    metrics.write("\n");
                }
                System.out.println("Step SPEA2 " + i + ", objective1=" + population.get(0).getObjective(0)
                        + ", objective2=" + population.get(0).getObjective(1));

            }
        }

        metrics.close();

    }

}
