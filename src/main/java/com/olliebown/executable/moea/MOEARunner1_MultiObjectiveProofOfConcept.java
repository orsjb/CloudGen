package com.olliebown.executable.moea;

import com.olliebown.evaluation.DeciderMOEAGrammar;
import com.olliebown.evaluation.DeciderMOEAProblem;
import com.olliebown.evaluation.metrics.AverageMovement;
import com.olliebown.evaluation.metrics.LowerStatesZero;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Proof of concept of the evolution. We are just trying to maximise amount of movement of the outputs
 */
public class MOEARunner1_MultiObjectiveProofOfConcept {

    public static void main(String[] args) throws IOException {

        FileUtil util = new FileUtil();

        DeciderMOEAProblem theProblem = new DeciderMOEAProblem(2, "data/Redgate") {
            @Override
            public double[] evaluate(List<Number[][]> outputData, Decider d) {
                double[] results = new double[numberOfObjectives];
                if(outputData != null) {
                    AverageMovement metric1 = new AverageMovement();
                    results[0] = -1 * metric1.getMetric(outputData)[0];
                    LowerStatesZero metric2 = new LowerStatesZero();
                    results[1] = -1 * metric2.getMetric(outputData)[0];
                }
                return results;
            }
        };

        Properties properties = new Properties();
        properties.setProperty("populationSize", "100");
        Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
                "NSGAII", properties, theProblem);

        int steps = 20000;
        int writeInterval = 1;
        for(int i = 0; i < steps; i++) {
            algorithm.step();
            if (i % writeInterval == 0) {
                NondominatedPopulation population = algorithm.getResult();
                for(int sol = 0; sol < population.size(); sol++) {
                    Solution s = population.get(sol);
                    Decider d = DeciderMOEAGrammar.generateDecider(s);
                    util.write(d,"gen" + i + "#" + sol);
                }
                System.out.println("Step " + i + ", objective1=" + population.get(0).getObjective(0)
                        + ", objective2=" + population.get(0).getObjective(1));
            }
        }

    }

}
