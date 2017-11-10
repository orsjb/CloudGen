package com.olliebown.utils;

import java.io.*;
import java.util.Date;

public class FileUtil {

    String dir;

    public FileUtil() {
        String mainObjectsDir = "../EvolvedObjects";
        File mainObjectsDirFile = new File(mainObjectsDir);
        if(!mainObjectsDirFile.exists()) {
            mainObjectsDirFile.mkdir();
        }
        File dataFolder = new File(mainObjectsDir + "/" + new Date().toString().replace(" ", "_"));
        dataFolder.mkdir();
        dir = dataFolder.getAbsolutePath();
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

}
