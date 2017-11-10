package com.olliebown.executable;

import com.olliebown.evaluation.DeciderMOEAGrammar;
import com.olliebown.evaluation.DeciderMOEAProblem;
import com.olliebown.evaluation.metrics.AverageMovement;
import com.olliebown.evaluation.metrics.MultiRunOutputVariance;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MOEARunner1 {

    public static void main(String[] args) throws IOException {

        FileUtil util = new FileUtil();

        DeciderMOEAProblem theProblem = new DeciderMOEAProblem() {
            @Override
            public double[] evalute(List<Number[][]> outputData) {
                double[] results = new double[5];
                if(outputData != null) {
                    AverageMovement metric1 = new AverageMovement();
                    results[0] = -1 * metric1.getMetric(outputData)[0];
                    MultiRunOutputVariance metric2 = new MultiRunOutputVariance();
                    results[1] = -1 * metric2.getMetric(outputData)[0];
                }
                return results;
            }
        };

        Properties properties = new Properties();
        properties.setProperty("populationSize", "10");
        Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
                "NSGAII", properties, theProblem);

        int steps = 20000;
        for(int i = 0; i < steps; i += 100) {
            algorithm.step();
            NondominatedPopulation population = algorithm.getResult();
            Solution s = population.get(0);
            Decider d = DeciderMOEAGrammar.generateDecider(s);
            util.write(d, i);
        }

        //write the results?


    }

}
