package com.example.mxaudiorecogition;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class Util {

    static String convertInputStreamToString(InputStream inputStream) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = r.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e("error converting input stream to string", e);
        }
        return null;
    }

    static void destroyProcess(Process process) {
        if (process != null) {
            try {
                process.destroy();
            } catch (Exception e) {
                Log.e("progress destroy error", e);
            }
        }
    }

    static boolean killAsync(AsyncTask asyncTask) {
        return asyncTask != null && !asyncTask.isCancelled() && asyncTask.cancel(true);
    }

    static boolean isProcessCompleted(Process process) {
        try {
            if (process == null) return true;
            process.exitValue();
            return true;
        } catch (IllegalThreadStateException e) {
            // do nothing
        }
        return false;
    }



}
