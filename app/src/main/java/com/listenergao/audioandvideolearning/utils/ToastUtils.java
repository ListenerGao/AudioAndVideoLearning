package com.listenergao.audioandvideolearning.utils;

import android.widget.Toast;

import com.listenergao.audioandvideolearning.LearningApplication;

public class ToastUtils {


    public static void toast(int resId) {
        toast(LearningApplication.getContext().getString(resId), Toast.LENGTH_SHORT);
    }

    public static void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    public static void toast(int resId, int duration) {
        toast(LearningApplication.getContext().getString(resId), duration);
    }

    private static void toast(String msg, int duration) {
        Toast.makeText(LearningApplication.getContext(), msg, duration).show();
    }
}
