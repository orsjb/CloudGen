package com.olliebown.evaluation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioSimulationEnvironment {

    List<Number[][]> allResults;

    public void loadAudioData(String dataDir) {
        File dir = new File(dataDir);
        File[] list = dir.listFiles();
        allResults = new ArrayList<Number[][]>();
        Gson gson = new Gson();
        for(File f : list) {
            if(f.getAbsolutePath().endsWith(".json")) {
                try {
                    FileReader in = new FileReader(f);
                    Number[][] results = gson.fromJson(in, Number[][].class);
                    allResults.add(results);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
