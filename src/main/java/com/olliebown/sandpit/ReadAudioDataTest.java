package com.olliebown.sandpit;

import com.google.gson.Gson;
import com.olliebown.evaluation.AudioSimulationEnvironment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadAudioDataTest {

    public static void main(String[] args) {
        File dir = new File("data/Redgate");
        File[] files = dir.listFiles();
        for(File f : files) {
            try {
                if(f.getAbsolutePath().endsWith(".json")) {
                    Gson gson = new Gson();
                    FileReader in = new FileReader(f);
                    Number[][] results = gson.fromJson(in, Number[][].class);
                    System.out.println("read results " + f.getAbsolutePath());
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        AudioSimulationEnvironment env = new AudioSimulationEnvironment();
//        env.loadAudioData("data/Redgate");
    }
}
