package com.olliebown.executable;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.util.List;

public class LookAtMetrics {

    public static void main(String[] args) {
        AudioSimulationEnvironment env = new AudioSimulationEnvironment();
        env.loadAudioData("data/Redgate");
        Decider d = Decider.read("/Users/olliebown/Dropbox/Resources/EvolvedObjects/Decision Trees/Fri_Nov_10_072617_UTC_2017/gen500#5");
        List<Number[][]> input = env.getInputData();
        List<Number[][]> output = env.generateAllOutputData(d);
        FileUtil.writeCSV("../EvolvedObjects/tmp_in.csv", input.get(4));
        FileUtil.writeCSV("../EvolvedObjects/tmp_out.csv", output.get(4));

    }
}
