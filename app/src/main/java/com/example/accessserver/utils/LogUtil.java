package com.example.accessserver.utils;

import android.util.Log;

public final class LogUtil {
    private static String TAG = LogUtil.class.getSimpleName();
    private static boolean isShowLog = true;

    public static void init(){
        isShowLog = true;
    }

    public static void i(Object oTag, String msg){
        if(!isShowLog){
            return;
        }
        String tag = getTagString(oTag);
        Log.i(tag, msg);
    }

    public static void i(String msg){
        i(TAG, msg);
    }

    public static void d(Object oTag, String msg){
        if(!isShowLog){
            return;
        }
        String tag = getTagString(oTag);
        Log.d(tag, msg);
    }

    public static void d(String msg){
        d(TAG, msg);
    }

    public static void e(Object oTag, String msg){
        if(!isShowLog){
            return;
        }
        String tag = getTagString(oTag);
        Log.e(tag, msg);
    }

    public static void e(String msg){
        e(TAG, msg);
    }

    private static String getTagString(Object oTag) {
        String tag;
        if(oTag instanceof String){
            tag = (String) oTag;
        }else{
            tag = oTag.getClass().getSimpleName();
        }
        return tag;
    }
}
