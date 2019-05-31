package com.example.mxaudiorecogition.com.mxaudiorecognition.client;

import java.io.File;

public final class ClientUtils {

    public static boolean isAndroid() {
        try {
            // This class is only available on android
            Class.forName("android.app.Activity");
            return true;
        } catch (ClassNotFoundException e) {
            // the class is not found when running JVM
            return false;
        }
    }

    public static String enrichDecoderCommand(String command) {
        if (isAndroid()) {
            String tempDirectory = System.getProperty("java.io.tmpdir");
            File f = new File(tempDirectory, "ffmpeg");
            if (f.exists() && f.length() > 1000000 && f.canExecute()) {
                return f.getAbsoluteFile().getParent() + "/" + command;
            }
            return null;
        } else {
            return command;
        }
    }

    public static String[] setPipeArtifacts() {
        String pipeEnvironment = null;
        String pipeArgument = null;
        if (System.getProperty("os.name").indexOf("indows") > 0) {
            pipeEnvironment = "cmd.exe";
            pipeArgument = "/C";
        } else {
            if (new File("/bin/bash").exists()) {
                pipeEnvironment = "/bin/bash";
                pipeArgument = "-c";
            } else {
                if (new File("/system/bin/sh").exists()) {
                    pipeEnvironment = "/system/bin/sh";
                    pipeArgument = "-c";
                }
            }
        }
        return new String[] { pipeEnvironment, pipeArgument };
    }

    private ClientUtils() {
        // Do not instantiate!
    }

}
