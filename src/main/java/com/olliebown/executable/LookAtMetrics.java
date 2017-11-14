package com.olliebown.executable;

import com.olliebown.evaluation.AudioSimulationEnvironment;
import com.olliebown.utils.FileUtil;
import net.happybrackets.patternspace.dynamic_system.decider.Decider;

import java.util.List;

public class LookAtMetrics {

    public static void main(String[] args) {
        AudioSimulationEnvironment env = new AudioSimulationEnvironment();
//        env.loadAudioData("data/Hayward");
        env.loadAudioData("data/Training_short");
//        Decider d = Decider.read("/Users/olliebown/Dropbox/Resources/EvolvedObjects/Decision Trees/Fri_Nov_10_072617_UTC_2017/gen500#5");
//        Decider d = Decider.read("/Users/olliebown/Dropbox/Resources/EvolvedObjects/Decision Trees/Mon_Nov_13_015118_UTC_2017/gen_NSGAII430#95");
//        Decider d = Decider.read("../EvolvedObjects/Tue_Nov_14_201443_AEDT_2017/gen_0_#0");
        Decider d = Decider.read("../EvolvedObjects/Tue_Nov_14_205858_AEDT_2017/gen_1200");
        List<Number[][]> input = env.getInputData();
        List<Number[][]> output = env.generateAllOutputData(d);
        FileUtil.writeCSV("../EvolvedObjects/tmp_in.csv", input.get(2));
        FileUtil.writeCSV("../EvolvedObjects/tmp_out.csv", output.get(2));
        System.out.println(d.getGenotypeString());
        Decider.printOut(d);

    }
}
