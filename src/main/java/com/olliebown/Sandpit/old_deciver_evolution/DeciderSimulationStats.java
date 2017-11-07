package com.olliebown.Sandpit.old_deciver_evolution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.happybrackets.patternspace.dynamic_system.decider.Decider;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DeciderSimulationStats {


	public static class DeciderSimulationStatsData implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		public int timeStepsRun;				//Total time
		
		
		public float timeAtZeroNode;			//Total time spent at node zero
		public float nodesVisited;				//Total number of nodes visited at least once
		public float medianNumVisits;			//The median number of visits out of nodes visited at least once
		public float maxNumVisits;				//The largest number of visits
		public float numMinimallyShortHolds;	//Number of visits to a node that lasted 1 time step
		public float changeRatio;				//Proportion of timesteps that involved a change of node
		public float medianMaxGapLength;		//For each node visited, work out the longest gap between visits, this is the median of all results
		public float traversalLinearity;		//How linearly does the system traverse the tree during the simulation
		public float entropy;					//the entropy measured over total time
		
		public DeciderSimulationStatsData() {
		}
	}

	Map<Integer, ArrayList<Integer>> nodeVisitTimes;
	ArrayList<Integer> nodeAtTime;
	Map<Integer, Integer> holdLengthHistogram;	//map of hold lengths to numbers of each hold length
	public List<float[]> stateHistory = new ArrayList<float[]>();		//all states over time
	Decider decider;

	DeciderSimulationStatsData data = new DeciderSimulationStatsData();

	public DeciderSimulationStats(Decider d) {
		decider = d;
		nodeVisitTimes = new Hashtable<Integer, ArrayList<Integer>>();
		nodeAtTime = new ArrayList<Integer>();
		data.timeStepsRun = 0;
	}
	
	protected void notifyNodeEvent(int node) {
		ArrayList<Integer> visitTimes = nodeVisitTimes.get(node);
		if(visitTimes == null) {
			visitTimes = new ArrayList<Integer>();
			nodeVisitTimes.put(node, visitTimes);
		}
		visitTimes.add(data.timeStepsRun);
		nodeAtTime.add(node);
		//grab the state from the decider (hidden elements only)
		float[] state = new float[decider.getNumHiddenElements()];
//		System.out.print("Decider num hidden elements " + state.length +  ":   " );
		for(int i = 0; i < decider.getNumHiddenElements(); i++) {
			state[i] = decider.getOutputs()[i].floatValue();
//			System.out.print(state[i] + " " );
		}
		stateHistory.add(state);
//		System.out.println();
		data.timeStepsRun++;
	}
	
	public void doStats() {
		//nodes visited
		data.nodesVisited = nodeVisitTimes.size();
		//time at zero node
		data.timeAtZeroNode = 0;
		if(nodeVisitTimes.containsKey(0)) {
			data.timeAtZeroNode = nodeVisitTimes.get(0).size() / (float)data.timeStepsRun;
		}
		//median num visits
		int[] visitCounts = new int[(int)data.nodesVisited];
		int index = 0;
		for(ArrayList<Integer> visitCount : nodeVisitTimes.values()) {
			visitCounts[index++] = visitCount.size();
		}
		Arrays.sort(visitCounts);
		data.medianNumVisits = visitCounts[visitCounts.length / 2];
		data.maxNumVisits = visitCounts[visitCounts.length - 1];
		//change ratio (grab state lengths)
		int lastNode = 0;
		int changeCount = 0;
		int currentStateLength = 0;
		holdLengthHistogram = new TreeMap<Integer, Integer>();
		for(int i : nodeAtTime) {
			if(i != lastNode) {
				changeCount++;
				lastNode = i;
				if(currentStateLength != 0) {
					int currentCount = 0;
					if(holdLengthHistogram.containsKey(currentStateLength)) {
						currentCount = holdLengthHistogram.get(currentStateLength);
					}
					holdLengthHistogram.put(currentStateLength, currentCount + 1);
				}
				currentStateLength = 1;
			} else {
				currentStateLength++;
			}
		}
		int currentCount = 0;
		if(holdLengthHistogram.containsKey(currentStateLength)) {
			currentCount = holdLengthHistogram.get(currentStateLength);
		}
		holdLengthHistogram.put(currentStateLength, currentCount + 1);
		data.changeRatio = (float)changeCount / (float)data.timeStepsRun;
		//num minimally short holds
		data.numMinimallyShortHolds = 0;
		if(holdLengthHistogram.containsKey(1)) {
			data.numMinimallyShortHolds = holdLengthHistogram.get(1);
		}
		//get gaps betwen visits (back to a node), map of nodes against list of gap lengths
		Map<Integer, ArrayList<Integer>> gapsBetweenVisits = new Hashtable<Integer, ArrayList<Integer>>();
		Map<Integer, Integer> lastTimeVisited = new Hashtable<Integer, Integer>();
		int time = 0;
		data.traversalLinearity = 0;
		for(int i : nodeAtTime) {
			//find out last time visited i
			int gapLength = time;
			if(lastTimeVisited.containsKey(i)) {
				gapLength = time - lastTimeVisited.get(i);
			}
			//add to gap list for i if greater than 1
			if(gapLength > 1) {
				ArrayList<Integer> gapsForIndex = gapsBetweenVisits.get(i);
				if(gapsForIndex == null) {
					gapsForIndex = new ArrayList<Integer>();
					gapsBetweenVisits.put(i, gapsForIndex);
				}
				gapsForIndex.add(gapLength);
			}
			lastTimeVisited.put(i, time);
			float nodeAsFract = (float)i / (float)decider.getNumLeaves();
			float timeAsFract = (float)time / (float)data.timeStepsRun;
			data.traversalLinearity += Math.pow(1f - Math.abs(nodeAsFract - timeAsFract) / 2f, 2f);
			time++;
		}
		data.traversalLinearity /= (float)nodeAtTime.size();
		//what is the median of the maximum gap lengths?
		//get maximum gap lengths
		if(gapsBetweenVisits.size() == 0) {
			data.medianMaxGapLength = 0;
		} else {
			int[] maxGapLengths = new int[gapsBetweenVisits.size()];	//list of max gap lengths (does not store node id)
			int next = 0;
			for(ArrayList<Integer> gapList : gapsBetweenVisits.values()) {
				//get max
				int max = 0;
				for(int i : gapList) {
					if(i > max) max = i;
				}
				maxGapLengths[next++] = max;
			}
			Arrays.sort(maxGapLengths);
			data.medianMaxGapLength = maxGapLengths[maxGapLengths.length / 2];
		}
		
		//..entropy
		
		int states = 10;	//limit number of states to simplify
		double[][] prob = new double[states][states];
		
		//first work out prob of each transfer
		int count = nodeAtTime.size() - 1;
		for(int i = 0; i < count; i++) {
			int n1 = nodeAtTime.get(i) % states; //simplified
			int n2 = nodeAtTime.get(i+1) % states;
			prob[n1][n2] += 1. / count;
		}
		
		//now calcuate entropy across all probs
		data.entropy = 0;
		for(int i = 0; i < states; i++) {
			for(int j = 0; j < states; j++) {
				if(prob[i][j] != 0)	data.entropy -= prob[i][j] * Math.log(prob[i][j]);
			}
		}
		
		
	}
	
	public void printStats() {
		System.out.println("Time Steps          : " + data.timeStepsRun);
		System.out.println("Num nodes           : " + decider.getNumLeaves());
		System.out.println("Time at zero        : " + data.timeAtZeroNode);
		System.out.println("Nodes visited       : " + data.nodesVisited + " (" + ((float)data.nodesVisited / decider.getNumLeaves()) + ")");
		System.out.println("Median num visits   : " + data.medianNumVisits);
		System.out.println("Maximum num visits  : " + data.maxNumVisits + " (" + (float)data.maxNumVisits / data.timeStepsRun + ")");
		System.out.println("Change ratio        : " + data.changeRatio);
		System.out.println("Num short holds     : " + data.numMinimallyShortHolds + " (" + (float)data.numMinimallyShortHolds / (float)data.timeStepsRun + ")");
		System.out.println("Median max gap      : " + data.medianMaxGapLength + " (" + data.medianMaxGapLength / (float)data.timeStepsRun + ")");
		System.out.println("Traversal linearity : " + data.medianMaxGapLength + " (" + data.medianMaxGapLength / (float)data.timeStepsRun + ")");
		System.out.println("Entropy             : " + data.entropy);
		
	}

	public String stateHistoryToGNUPlotString() {
		StringBuffer buff = new StringBuffer();
		int t = 0;
		for(float[] state : stateHistory) {
			for(int i = 0; i < state.length; i++) {
//				buff.append(t + " " + i + " " + state[i] + "\n");			//pm3d format
				buff.append(state[i] + " ");
			}
			t++;
			buff.append("\n");
		}
		return buff.toString();
	}
	
	public Dataset stateHistoryToDataSet() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		int numSeries = stateHistory.get(0).length;
		XYSeries[] series = new XYSeries[numSeries];
		for(int i = 0; i < series.length; i++) {
			series[i] = new XYSeries("State " + i);
	        dataset.addSeries(series[i]);
		}
		int t = 0;
		for(float[] state : stateHistory) {
			for(int i = 0; i < state.length; i++) {
				series[i].add(t, state[i]);
			}
			t++;
		}
		return dataset;
	}
	
	public String decisionHistoryToGNUPlotString() {
		StringBuffer buff = new StringBuffer();
		int t = 0;
		for(int x : nodeAtTime) {
			buff.append(x + " \n");
		}
		return buff.toString();
	}
	
	public String statsToGNUPlotString() {
		StringBuffer buff = new StringBuffer();
		buff.append(data.timeStepsRun + " ");
		buff.append(decider.getNumLeaves() + " ");
		buff.append(data.timeAtZeroNode + " ");
		buff.append(data.nodesVisited + " ");
		buff.append(data.medianNumVisits + " ");
		buff.append(data.maxNumVisits + " ");
		buff.append(data.changeRatio + " ");
		buff.append(data.numMinimallyShortHolds + " ");
		buff.append(data.medianMaxGapLength + " ");
		buff.append(data.traversalLinearity);
		buff.append("\n");
		return buff.toString();
	}
	
	public String holdLengthHistogramToGNUPlotString() {
		StringBuffer buff = new StringBuffer();
		for(int key : holdLengthHistogram.keySet()) {
			int val = holdLengthHistogram.get(key);
			buff.append(key + " " + val + "\n");
		}
		return buff.toString();
	}
	
	public void printHoldLengthHistogram() {
		System.out.println("--- hold lengths -----");
		int sum = 0;
		for(int holdLength : holdLengthHistogram.keySet()) {
			int count = holdLengthHistogram.get(holdLength);
			System.out.println(holdLength + " " + count);
			sum += holdLength * count;
		}
		System.out.println("Sum: " + sum);
		System.out.println("----------------------");
	}
	
	public static DeciderSimulationStatsData average(List<DeciderSimulationStatsData> list) {
		DeciderSimulationStatsData average = new DeciderSimulationStatsData();
		for(DeciderSimulationStatsData dss : list) {
			average.changeRatio += dss.changeRatio;
			average.maxNumVisits += dss.maxNumVisits;
			average.medianNumVisits += dss.medianNumVisits;
			average.nodesVisited += dss.nodesVisited;
			average.numMinimallyShortHolds += dss.numMinimallyShortHolds;
			average.timeAtZeroNode += dss.timeAtZeroNode;
			average.timeStepsRun += dss.timeStepsRun;
		}
		int size = list.size();
		average.changeRatio /= size;
		average.maxNumVisits /= size;
		average.medianNumVisits /= size;
		average.nodesVisited /= size;
		average.numMinimallyShortHolds /= size;
		average.timeAtZeroNode /= size;
		average.timeStepsRun /= size;
		return average;
	}
	
	public DeciderSimulationStatsData getData() {
		return data;
	}

	public int getTimeStepsRun() {
		return data.timeStepsRun;
	}

	public float getTimeAtZeroNode() {
		return data.timeAtZeroNode;
	}

	public float getNodesVisited() {
		return data.nodesVisited;
	}

	public float getMedianNumVisits() {
		return data.medianNumVisits;
	}

	public float getMaxNumVisits() {
		return data.maxNumVisits;
	}

	public float getNumMinimallyShortHolds() {
		return data.numMinimallyShortHolds;
	}

	public float getChangeRatio() {
		return data.changeRatio;
	}
	
	public float getMedianMaxGapLength() {
		return data.medianMaxGapLength;
	}

	public float getTraversalLinearity() {
		return data.traversalLinearity;
	}
	
}
