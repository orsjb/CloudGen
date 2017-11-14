package com.olliebown.executable.homespun;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.evaluation.EvaluationMetric;
import com.olliebown.evaluation.metrics.NumNodesVisited;
import com.olliebown.sandpit.basic_decider_evolution.FitnessFunction;
import com.olliebown.sandpit.basic_decider_evolution.MaximiseVarianceFitnessFunction;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class HomeSpunGA_JustLeavesVisited {

	public static void main(String[] args) {

        FileUtil util = new FileUtil();
        util.writeText("HomeSpunGA_JustLeavesVisited", "info.txt");

		int popSize = 100;
		int iterations = 1000000;

		AudioSimulationEnvironment env = new AudioSimulationEnvironment();
		env.loadAudioData("data/Training");

		NumNodesVisited nodesVisited = new NumNodesVisited();

		Random rng = new Random();
		Decider[] pop = new Decider[popSize];
		double[] fitness = new double[popSize];
		int fittest = 0;
		//init
		for(int i = 0; i < popSize; i++) {
			pop[i] = Decider.newRandomTree(6, 30, rng, 0.05f);
            fitness[i] = nodesVisited.getMetric(env.generateAllOutputData(pop[i]))[0];
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
			fitness[loser] = nodesVisited.getMetric(env.generateAllOutputData(pop[loser]))[0];
			if(fitness[loser] > fitness[fittest]) fittest = loser;
			if(time % 100 == 0) {
				System.out.println("----------------------");
				System.out.println("fittest : fitness=" + fitness[fittest]);
				util.write(pop[fittest], "gen_" + time);
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
