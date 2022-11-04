package com.listenergao.audioandvideolearning.utils

import com.listenergao.audioandvideolearning.utils.ToastUtils
import com.listenergao.audioandvideolearning.LearningApplication
import android.widget.Toast

/**
 * @author listenergao
 */
object ToastUtils {

    fun toast(resId: Int) {
        toast(LearningApplication.getContext().getString(resId), Toast.LENGTH_SHORT)
    }

    fun toast(msg: String) {
        toast(msg, Toast.LENGTH_SHORT)
    }

    fun toast(resId: Int, duration: Int) {
        toast(LearningApplication.getContext().getString(resId), duration)
    }

    fun toast(msg: String, duration: Int) {
        Toast.makeText(LearningApplication.getContext(), msg, duration).show()
    }
}