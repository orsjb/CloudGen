package com.olliebown.evaluation;

import com.google.gson.Gson;
import net.happybrackets.patternspace.dynamic_system.core.DynamicSystem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
        Number[][] outputData = new Number[inputData.length][inputData[0].length];
        for(int i = 0; i < inputData.length; i++) {
            ds.update(inputData[i]);
            Number[] outputStep = ds.getOutputs();
            for(int j = 0; j < outputStep.length; j++) {
                outputData[i] = outputStep;
            }
        }
        return outputData;
    }

}
