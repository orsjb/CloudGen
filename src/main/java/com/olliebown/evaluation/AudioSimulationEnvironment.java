package com.olliebown.evaluation;

import com.google.gson.Gson;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystemUtils;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AudioSimulationEnvironment {

    List<Number[][]> inputData;

    public void loadAudioData(String dataDir) {
        File dir = new File(dataDir);
        File[] list = dir.listFiles();
        inputData = new ArrayList<Number[][]>();
        Gson gson = new Gson();
        for(File f : list) {
            if(f.getAbsolutePath().endsWith(".json")) {
                try {
                    FileReader in = new FileReader(f);
                    Number[][] results = gson.fromJson(in, Number[][].class);
                    inputData.add(results);
                    System.out.println("Loaded audio data file: " + f.getAbsolutePath());
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Number[][]> generateAllOutputData(DynamicSystem ds) {
        List<Number[][]> outputData = new ArrayList<>();
        for(int i = 0; i < inputData.size(); i++) {
            ds.reset();
            outputData.add(generateOutputData(ds, inputData.get(i)));
        }
        return outputData;
    }

    public Number[][] generateOutputData(DynamicSystem ds, Number[][] inputData) {
        Number[][] outputData = new Number[inputData.length][];
        for(int i = 0; i < inputData.length; i++) {
            ds.update(inputData[i]);
//            outputData[i] = DynamicSystemUtils.getOutputs(ds, Double.class);
            outputData[i] = ds.getOutputs().clone();
        }
        return outputData;
    }

    public List<Number[][]> getInputData() {
        return inputData;
    }


    public List<Number[][]> generateAllOutputDataSubset(DynamicSystem ds) {
        //create a subset permutation of the outputData.
        LinkedList<Number[][]> inputDataSubset = new LinkedList<>();
        for(Number[][] n : inputData) {
            inputDataSubset.add(n);
        }
        //randomly remove half the data from the subset
        for(int i = 0; i < inputData.size() / 2; i++) {
            int random = (int)(Math.random() * inputDataSubset.size());
            inputDataSubset.remove(random);
        }
        List<Number[][]> outputData = new ArrayList<>();
        for(int i = 0; i < inputDataSubset.size(); i++) {
            ds.reset();
            outputData.add(generateOutputData(ds, inputDataSubset.get(i)));
        }
        return outputData;
    }
}
