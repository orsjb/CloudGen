package com.olliebown.utils;

import java.io.*;
import java.util.Date;

public class FileUtil {

    public String dir;

    public FileUtil() {
        String mainObjectsDir = "../EvolvedObjects";
        File mainObjectsDirFile = new File(mainObjectsDir);
        if(!mainObjectsDirFile.exists()) {
            mainObjectsDirFile.mkdir();
        }
        File dataFolder = new File(mainObjectsDir + "/" + new Date().toString().
                replace(" ", "_").
                replace(":", "").
                replace("/", ""));
        dataFolder.mkdir();
        dir = dataFolder.getAbsolutePath();
    }

    public void writeText(String text, String name) {
        try {
            PrintStream ps = new PrintStream(new File(dir + "/" + name));
            ps.print(text);
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Serializable object, String name) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(dir + "/" + name));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeCSV(String file, Number[][] data) {
        try {
            PrintStream ps = new PrintStream(new File(file));
            for(int i = 0; i < data.length; i++) {
                for(int j = 0; j < data[i].length - 1; j++) {
                    ps.print(data[i][j] + ",");
                }
                ps.println(data[i][data[i].length - 1]);
            }

            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
