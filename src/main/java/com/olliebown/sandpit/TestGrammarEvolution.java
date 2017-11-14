package com.olliebown.sandpit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TestGrammarEvolution {

    public static void main(String[] args) throws IOException {

//        FileWriter writer = new FileWriter(new File("../EvolvedObjects/test.txt"));
//        writer.write("Hello!");
//        writer.flush();
        Set<Integer> integerSet = new HashSet<>();
        integerSet.add(1);
        integerSet.add(1);
        System.out.println(integerSet.size());

    }
}
