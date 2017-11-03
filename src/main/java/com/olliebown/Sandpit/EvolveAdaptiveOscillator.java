package com.olliebown.Sandpit;

import net.happybrackets.patternspace.dynamic_system.ctrnn.Chromosome;
import net.happybrackets.patternspace.dynamic_system.ctrnn.Ctrnn;
import net.happybrackets.patternspace.dynamic_system.ctrnn.CtrnnNode;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;

import java.io.*;
import java.util.Random;

public class EvolveAdaptiveOscillator {


    static final double TWO_PI = 2 * Math.PI;
    static final double MS_PER_STEP = 20;
    static final int MAX_TRIALS = 20;
    static final int DRIVE_TIME = 2000;
    static final int RUN_TIME = 3000;
    static final Random rng = new Random();

    public static class FitnessFunc extends FitnessFunction {

        Ctrnn.Params params;

        public FitnessFunc(Ctrnn.Params params) {
            this.params = params;
        }

        @Override
        protected double evaluate(IChromosome a_subject) {

            Ctrnn ctrnn = new Ctrnn(Chromosome.fromJGAPChromosome(a_subject), params);
            return evaluatePrint(ctrnn, false, null);
        }

        public double evaluatePrint(Ctrnn ctrnn, boolean print, String graphOutput) {
            FileOutputStream fos = null;
            PrintStream ps = null;
            if(graphOutput != null && !graphOutput.equals("")) {
                try {
                    fos = new FileOutputStream(new File(graphOutput));
                    ps = new PrintStream(fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            ctrnn.resetZero();
            float productScore = 1;
            float avgScore = 0;
            for(int trial = 0; trial < MAX_TRIALS; trial++) {
                double thisTrialScore = 0;
                //aim of each trial is to score the ctrnn on its entrainment
                //run with an input for some time, then check the period of the ctrnn oscillation
                //choose period
                int periodInMS = (int)(rng.nextFloat() * 2800 + 400);
                int periodInTimeSteps = (int)(periodInMS / MS_PER_STEP);
                float phaseX = rng.nextFloat();
                float phaseY = rng.nextFloat();
                float phaseZ = rng.nextFloat();
                double[] outputData = new double[DRIVE_TIME+RUN_TIME];
                //3-axis data, normalised
                int t;
                Number[] inputs = new Number[3];
                for(t = 0; t < DRIVE_TIME; t++) {
                    inputs[0] = (float)(Math.sin((t / (float)periodInTimeSteps + phaseX) * TWO_PI));
                    inputs[1] = (float)(Math.sin((t / (float)periodInTimeSteps + phaseY) * TWO_PI));
                    inputs[2] = (float)(Math.sin((t / (float)periodInTimeSteps + phaseZ) * TWO_PI));
                    ctrnn.update(inputs);
                    double out = ctrnn.getOutput(0);
//                    outputData[t] = out;                  //don't collect on driving phase
                    if(ps != null) {
                        ps.println(t + "," + inputs[0] + "," + inputs[1] + "," + inputs[2] + "," + out);
                    }
                }
                //set inputs to zero now? Or random?
                inputs[0] = 0;//rng.nextFloat() * 2f - 1f;
                inputs[1] = 0;//rng.nextFloat() * 2f - 1f;
                inputs[2] = 0;//rng.nextFloat() * 2f - 1f;
                for(; t < RUN_TIME+DRIVE_TIME; t++) {
                    ctrnn.update(inputs);
                    double out = ctrnn.getOutput(0);
                    outputData[t] = out;
                    if(ps != null) {
                        ps.println(t + "," + inputs[0] + "," + inputs[1] + "," + inputs[2] + "," + out);
                    }
                }
                //analyse the data
                //now we have outputData populated and we know the period.
                //work out the period of the CTRNN oscillations
                //OK just to look at zero xxings.
                int lastXing = -1;
                double lastVal = outputData[0];
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for(t = 1; t < RUN_TIME+DRIVE_TIME; t++) {
                    //just look at - to + zeroxings?
                    if(lastVal < 0 && outputData[t] >= 0) {
                        if(lastXing != -1) {
                            stats.addValue(t - lastXing);
                        }
                        lastXing = t;
                    }
                    lastVal = outputData[t];
                }
                stats.addValue(t - lastXing);   //this last one ensures that the long tail is counted if the agent stops
                double measuredPeriodInTimeSteps = stats.getMean();
                if(print) {
                    System.out.println("               - Period " + periodInTimeSteps + ",\tMeasured " + measuredPeriodInTimeSteps + "\t(count " + stats.getN() + ",\tstdev " + stats.getStandardDeviation() + ")");
                }
                if(measuredPeriodInTimeSteps == 0 || Double.isNaN(measuredPeriodInTimeSteps) || Double.isInfinite(measuredPeriodInTimeSteps)) {
                    thisTrialScore = 0;
                } else {
                    thisTrialScore = 1f - (Math.abs(periodInTimeSteps - measuredPeriodInTimeSteps) / 10000f);
                    thisTrialScore = Math.max(thisTrialScore, 0);
                    thisTrialScore = Math.min(thisTrialScore, 1);
                    thisTrialScore = Math.pow(thisTrialScore, 2);
                }
                if(!Double.isNaN(thisTrialScore) && !Double.isInfinite(thisTrialScore)) {
                    productScore *= thisTrialScore;
                    avgScore += thisTrialScore / MAX_TRIALS;
                }
            }
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            return productScore;
            return avgScore;// + MAX_TRIALS * productScore;
        }
    }

    public static void main(String[] args) throws InvalidConfigurationException, IOException, ClassNotFoundException {
//        generateGraph();
        evolve();
    }

    public static void generateGraph() throws IOException, ClassNotFoundException {
        String destDir = "/Users/olliebown/Desktop";
        //read in CTRNN
        FileInputStream fis = new FileInputStream(new File(destDir + "/fittest_gen495"));
        ObjectInputStream ois = new ObjectInputStream(fis);
        Ctrnn ctrnn = (Ctrnn)ois.readObject();
        ois.close();
        fis.close();
        //create the fitness function
        FitnessFunc fitnessFunc = new FitnessFunc(ctrnn.params);
        //run it
        fitnessFunc.evaluatePrint(ctrnn, true, destDir + "/fittest_graph.csv");
    }

    public static void evolve() throws InvalidConfigurationException, IOException {
        String destDir = "out/output data";
        //create Ctrnn params
        Ctrnn.Params params = Ctrnn.Params.getDefault();
        params.inTransferFunc = CtrnnNode.TransferFunction.TANH;
        params.hTransferFunc = CtrnnNode.TransferFunction.TANH;
        params.numInputNodes = 3;
        params.numHiddenNodes = 8;
        params.numOutputNodes = 1;
        //???? try it out
        params.hTcMin = -0.3f;
        params.hTcMax = 3.0f;
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
        org.jgap.Chromosome sampleChromosome = new org.jgap.Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);
        conf.setPopulationSize(100);
        Genotype pop = Genotype.randomInitialGenotype(conf);
        for (int i = 0; i < 10000; i++) {
            System.out.println("------------------------------");
            System.out.print("gen " + i + ", ");
            FileOutputStream fos = new FileOutputStream(new File(destDir + "/fittest_gen" + i));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            IChromosome fittest = pop.getFittestChromosome();
            Ctrnn ctrnn = new Ctrnn(Chromosome.fromJGAPChromosome(fittest), params);
            oos.writeObject(ctrnn);
            oos.close();
            fos.close();
            //also write the chromosome
            FileOutputStream fos2 = new FileOutputStream(new File(destDir + "/fittest_gen_chromosome" + i));
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            oos2.writeObject(fittest);
            oos2.close();
            fos2.close();
            pop.evolve();
            // add current best fitness to chart
            double fitness = fittest.getFitnessValue();
            System.out.println("fitness: " + fitness);
            System.out.println("------------------------------");
            fitnessFunc.evaluatePrint(ctrnn, true, null);
        }

    }




}
