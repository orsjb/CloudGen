package com.olliebown.jctrnn.evolution;

import com.olliebown.ctrnn.JCtrnn;
import com.olliebown.ctrnn.JLi;
import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class EvolveAdaptiveOscillator {


    static final double TWO_PI = 2 * Math.PI;
    static final double MS_PER_STEP = 20;
    static final int MAX_TRIALS = 20;
    static final int DRIVE_TIME = 10000;
    static final int RUN_TIME = 10000;
    static final Random rng = new Random();

    public static class FitnessFunc extends FitnessFunction {

        JCtrnn.Params params;

        public FitnessFunc(JCtrnn.Params params) {
            this.params = params;
        }

        @Override
        protected double evaluate(IChromosome a_subject) {
            return evaluatePrint(a_subject, false);
        }

        public double evaluatePrint(IChromosome a_subject, boolean print) {
            JCtrnn ctrnn = new JCtrnn(com.olliebown.ctrnn.Chromosome.fromJGAPChromosome(a_subject), params);
            ctrnn.resetZero();
            float productScore = 1;
            float avgScore = 0;
            for(int trial = 0; trial < MAX_TRIALS; trial++) {
                float thisTrialScore = 0;
                //aim of each trial is to score the ctrnn on its entrainment
                //run with an input for some time, then check the period of the ctrnn oscillation
                //choose period
                int periodInMS = (int)(rng.nextFloat() * 2800 + 200);
                int periodInTimeSteps = (int)(periodInMS / MS_PER_STEP);
                float phaseX = rng.nextFloat();
                float phaseY = rng.nextFloat();
                float phaseZ = rng.nextFloat();
                float[] outputData = new float[RUN_TIME];
                //3-axis data, normalised
                int t;
                float[] inputs = new float[3];
                for(t = 0; t < DRIVE_TIME; t++) {
                    inputs[0] = (float)(Math.sin((t / periodInTimeSteps + phaseX) * TWO_PI));
                    inputs[1] = (float)(Math.sin((t / periodInTimeSteps + phaseY) * TWO_PI));
                    inputs[2] = (float)(Math.sin((t / periodInTimeSteps + phaseZ) * TWO_PI));
                    ctrnn.update(inputs);
                }
                //set inputs to zero now? Or random?
                inputs[0] = rng.nextFloat() * 2f - 1f;
                inputs[1] = rng.nextFloat() * 2f - 1f;
                inputs[2] = rng.nextFloat() * 2f - 1f;
                for(t = 0; t < RUN_TIME; t++) {
                    ctrnn.update(inputs);
                    outputData[t] = ctrnn.getOutput(0);
                }
                //analyse the data
                //now we have outputData populated and we know the period.
                //work out the period of the CTRNN oscillations
                //OK just to look at zero xxings.
                float measuredPeriodInTimeSteps = 0;
                int lastXing = -1;
                float lastVal = outputData[0];
                ArrayList<Integer> crossingIntervalsTS = new ArrayList<>();
                for(t = 1; t < RUN_TIME; t++) {
                    //just look at - to + zeroxings?
                    if(lastVal < 0 && outputData[t] >= 0) {
                        if(lastXing != -1) {
                            crossingIntervalsTS.add(t - lastXing);
                        }
                        lastXing = t;
                    }
                    lastVal = outputData[t];
                }
                for(int xing : crossingIntervalsTS) {
                    measuredPeriodInTimeSteps += (float)xing / (float)crossingIntervalsTS.size();
                }
                if(print) {
                    System.out.println("               - Period " + periodInTimeSteps + ", Measured " + measuredPeriodInTimeSteps);
                }
                //TODO - correct normalised score calc in exp
                if(measuredPeriodInTimeSteps == 0) {
                    thisTrialScore = 0;
                } else {
                    thisTrialScore = 1f - (Math.abs(periodInTimeSteps - measuredPeriodInTimeSteps) / 10000f);
                    thisTrialScore = Math.max(thisTrialScore, 0);
                    thisTrialScore = Math.min(thisTrialScore, 1);
                }
                productScore *= thisTrialScore;
                avgScore += thisTrialScore / MAX_TRIALS;
            }
//            return productScore;
            return avgScore + MAX_TRIALS * productScore;
        }
    }

    public static void main(String[] args) throws InvalidConfigurationException, IOException {

        String destDir = "out/output data";

        //create JCTRNN params
        JCtrnn.Params params = JCtrnn.Params.getDefault();
        params.inTransferFunc = JLi.TransferFunction.TANH;
        params.hTransferFunc = JLi.TransferFunction.SINTANH;
        params.numInputNodes = 3;
        params.numHiddenNodes = 4;
        params.numOutputNodes = 1;

        //create the fitness function
        FitnessFunc fitnessFunc = new FitnessFunc(params);

        //use JGAP to evolve
        Configuration conf = new DefaultConfiguration();
        conf.setPreservFittestIndividual(true);
        conf.setFitnessFunction(fitnessFunc);
        Gene[] sampleGenes = new Gene[params.getGenotypeLength()];
        for(int i = 0; i < sampleGenes.length; i++) {
            sampleGenes[i] = new DoubleGene(conf, 0, 1);
        }
        Chromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);
        conf.setPopulationSize(100);
        Genotype pop = Genotype.randomInitialGenotype(conf);
        for (int i = 0; i < 10000; i++) {
            System.out.println("------------------------------");
            System.out.print("gen " + i + ", ");
            FileOutputStream fos = new FileOutputStream(new File(destDir + "/fittest_gen" + i));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            IChromosome fittest = pop.getFittestChromosome();
            JCtrnn ctrnn = new JCtrnn(com.olliebown.ctrnn.Chromosome.fromJGAPChromosome(fittest), params);
            oos.writeObject(ctrnn);
            pop.evolve();
            // add current best fitness to chart
            double fitness = fittest.getFitnessValue();
            System.out.println("fitness: " + fitness);
            System.out.println("------------------------------");
            fitnessFunc.evaluatePrint(fittest, true);
        }


    }




}
