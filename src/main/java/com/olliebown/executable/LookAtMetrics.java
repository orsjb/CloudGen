package com.olliebown.executable;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.util.List;

public class LookAtMetrics {

    public static void main(String[] args) {
        AudioSimulationEnvironment env = new AudioSimulationEnvironment();
//        env.loadAudioData("data/Hayward");
        env.loadAudioData("data/Redgate");
//        Decider d = Decider.read("/Users/olliebown/Dropbox/Resources/EvolvedObjects/Decision Trees/Fri_Nov_10_072617_UTC_2017/gen500#5");
        Decider d = Decider.read("/Users/olliebown/Dropbox/Resources/EvolvedObjects/Decision Trees/Mon_Nov_13_015118_UTC_2017/gen_NSGAII430#95");
        List<Number[][]> input = env.getInputData();
        List<Number[][]> output = env.generateAllOutputData(d);
        FileUtil.writeCSV("../EvolvedObjects/tmp_in.csv", input.get(2));
        FileUtil.writeCSV("../EvolvedObjects/tmp_out.csv", output.get(2));
        System.out.println(d.getGenotypeString());

    }
}
