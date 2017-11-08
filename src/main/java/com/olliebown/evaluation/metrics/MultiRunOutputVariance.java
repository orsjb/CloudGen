package com.olliebown.evaluation.metrics;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.evaluation.EvaluationMetric;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiRunOutputVariance implements EvaluationMetric<List<Number[][]>> {

    int numChunkSizes = 10;
    int chunkSizeStep = 200;

    @Override
    public double[] getMetric(List<Number[][]> dsOutput) {
        double[] metric = new double[numChunkSizes];
        //input is multiple time series of device output, time series of various length
        //we're looking to get a sense of the output variance across and within runs
        //doing this across multiple chunk sizes
        for(int chunkIndex = 0; chunkIndex < numChunkSizes; chunkIndex++) {
            int chunkSize = (chunkIndex + 1) * chunkSizeStep;
            List<double[]> chunkCentroids = new ArrayList<>();
            double[] currentChunkCentroid = null;
            //run a sliding window over all the data with this chunk size
            for(int run = 0; run < dsOutput.size(); run++) {
                Number[][] runData = dsOutput.get(run);
                for(int t = 0; t < runData.length; t++) {
                    if(t % chunkSize == 0) {
                        if(currentChunkCentroid != null) {
                            chunkCentroids.add(currentChunkCentroid);
                        }
                        currentChunkCentroid = new double[runData[t].length];
                        if(runData.length - t < chunkSize) {
                            break;
                        }
                    }
                    Number[] features = runData[t];
//                    System.out.println("Features length is " + features.length);
                    for(int i = 0; i < features.length; i++) {
//                        System.out.print(features[i].doubleValue() + " ");
                        currentChunkCentroid[i] += features[i].doubleValue() / chunkSize;
                    }
                }
            }
            //now we have an array of all chunk centroids for the given chunk size
            //get the average distance
            double averageDistance = 0;
            int count = 0;
            for(int i = 0; i < chunkCentroids.size(); i++) {
                for(int j = 0; j < i; j++) {
                    double dist = DynamicSystemUtils.distance(chunkCentroids.get(i), chunkCentroids.get(j));
//                    System.out.println("Distance between chunks " + i + " and " + j + " (chunk size " + chunkSize + "): " + dist);
                    averageDistance += dist;
                    count++;
                }
            }
            metric[chunkIndex] = averageDistance / count;
        }
        return metric;
    }

    @Override
    public String[] getMetricInfo() {
        return null;
    }
}
