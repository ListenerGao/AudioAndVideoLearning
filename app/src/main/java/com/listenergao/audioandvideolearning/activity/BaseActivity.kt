package com.listenergao.audioandvideolearning.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.listenergao.audioandvideolearning.utils.ToastUtils

/**
 * @author listenergao
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
        ToastUtils.toast(msg, duration)
    }
}