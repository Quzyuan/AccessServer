package com.example.accessserver.utils;

import android.content.Context;
import android.widget.Toast;

public class ShowUtil {
    private static Toast toast;
    private static CharSequence oldMsg;
    private static long firstClickTime;
    private static long secondClickTime;

    public static void showToast(Context context, CharSequence msg) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
            oldMsg = msg;
            firstClickTime = System.currentTimeMillis();
        } else {
            secondClickTime = System.currentTimeMillis();
            if (msg.equals(oldMsg)) {
                if (secondClickTime - firstClickTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = msg;
                toast.setText(msg);
                toast.show();
            }
        }
        firstClickTime = secondClickTime;
    }
}
