package com.olliebown.sandpit.basic_decider_evolution;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Random;

public class GA {

	public static void main(String[] args) {

	    String mainObjectsDir = "../EvolvedObjects";
        File mainObjectsDirFile = new File(mainObjectsDir);
        if(!mainObjectsDirFile.exists()) {
            mainObjectsDirFile.mkdir();
        }
        File dataFolder = new File(mainObjectsDir + "/" + new Date().toString().replace(" ", "_"));
		dataFolder.mkdir();
		String dir = dataFolder.getAbsolutePath();
		
		int popSize = 100;
		int iterations = 1000000;
		FitnessFunction ff = new MaximiseVarianceFitnessFunction();
		Random rng = new Random();
		Decider[] pop = new Decider[popSize];
		double[] fitness = new double[popSize];
		int fittest = 0;
		//init
		for(int i = 0; i < popSize; i++) {
			pop[i] = Decider.newRandomTree(6, 30, rng, 0.05f);
			fitness[i] = ff.evaluate(pop[i]);
			if(fitness[i] > fitness[fittest]) fittest = i;
		}
		//run
		for(int time = 0; time < iterations; time++) {
			int a = rng.nextInt(popSize);
			int b = a;
			while(b == a) {
				b = rng.nextInt(popSize);
			}
			int winner = (fitness[a] > fitness[b]) ? a : b;
			int loser = (winner == a) ? b : a;
			pop[loser] = pop[winner].copyMutate();
			fitness[loser] = ff.evaluate(pop[loser]);
			if(fitness[loser] > fitness[fittest]) fittest = loser;
			if(time % 100 == 0) {
				System.out.println("----------------------");
				System.out.println("fittest : fitness=" + fitness[fittest]);
				write(dir, pop[fittest], time);
			}
		}
	}

	private static void write(String dir, Decider decider, int gen) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(dir + "/gen" + gen));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(decider);
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
