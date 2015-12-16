package com.eland.android.eoas.Util;

import android.util.Log;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class ConsoleUtil {

    private final static boolean DEBUG = true;

    //verbose
    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }

    }

    //debug
    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }

    }

    //info
    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }

    }

    //warn
    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }

    }
}
