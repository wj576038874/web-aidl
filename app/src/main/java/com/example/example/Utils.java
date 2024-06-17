package com.example.example;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

public class Utils {
    public static String getProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }
}
