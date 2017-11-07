package com.olliebown.sandpit;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.evaluation.metrics.MultiRunOutputVariance;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.io.FileOutputStream;
import java.util.List;

public class TestVarianceOutput {

    public static void main(String[] args) {

//        double[] x = new double[20], y = new double[20];
//        double distance = 0;
//        for(int i = 0; i < x.length; i++) {
//            x[i] = Math.random();
//            y[i] = Math.random();
//            distance += (x[i] - y[i]) * (x[i] - y[i]);
//
//        }
//        distance = Math.sqrt(distance);
//        System.out.println(DynamicSystemUtils.distance(x, y) + " " + distance);



        AudioSimulationEnvironment env = new AudioSimulationEnvironment();
        env.loadAudioData("data/Redgate");

        Decider d = Decider.read("/Users/olliebown/Dropbox/Resources/EvolvedObjects/Decision Trees/Tue_Nov_07_05:18:43_UTC_2017/gen300");


        List<Number[][]> results = env.generateAllOutputData(d);
//        Number[][] firstRun = results.get(0);
//        for(int i = 0; i < firstRun.length; i++) {
//            for(int j = 0; j < firstRun[i].length; j++) {
//                System.out.print(firstRun[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();

        MultiRunOutputVariance run = new MultiRunOutputVariance();
        double[] metric = run.getMetric(results);

        for(int i = 0; i < metric.length; i++) {
            System.out.println(metric[i]);
        }
    }
}
