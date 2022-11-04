package com.listenergao.audioandvideolearning.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.Size
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.listenergao.audioandvideolearning.R
import com.listenergao.audioandvideolearning.databinding.ActivityVideoRecordBinding
import com.listenergao.audioandvideolearning.utils.ToastUtils

/**
 * @description: 描述
 * @date: 2022/10/31 16:59
 * @author: ListenerGao
 */
class VideoRecordActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val REQUEST_CAMERA_CODE = 1025
    }

    private lateinit var mBinding: ActivityVideoRecordBinding

    private lateinit var mCamera: Camera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mBinding.btnOpenCamera.setOnClickListener(this)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_audio_record -> {
                if (checkPermission()) {
                    openCamera()
                }

            }
        }
    }

    private fun openCamera() {
        if (!::mCamera.isInitialized) {
            initCamera()
        }

    }

    private fun initCamera() {
        /**
         * int cameraId 摄像头ID
         * CameraInfo.CAMERA_FACING_BACK 后置摄像头
         * CameraInfo.CAMERA_FACING_FRONT 前置摄像头
         */
        mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK)
        // 设置相机参数
        val parameters = mCamera.parameters

        // 获取摄像头支持的所有的预览格式
        val supportedPreviewFormats = parameters.supportedPreviewFormats
        if (supportedPreviewFormats.contains(ImageFormat.NV21)) {
            parameters.previewFormat = ImageFormat.NV21
        } else {
            ToastUtils.toast("视频参数设置错误：设置预览图像格式异常")
        }

        // 获取摄像头支持的所有的预览尺寸
        val supportedPreviewSizes = parameters.supportedPreviewSizes
        val previewWidth = 640
        val previewHeight = 480
        if (isSupportedPreviewSizes(supportedPreviewSizes, previewWidth, previewHeight)) {
            parameters.setPreviewSize(previewWidth, previewHeight)
        } else {
            ToastUtils.toast("视频参数设置错误：设置预览尺寸异常")
        }

        try {
            mCamera.parameters = parameters
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.toast("视频参数设置错误")
        }


    }

    private fun isSupportedPreviewSizes(
        supportedPreviewSizes: List<Size>,
        width: Int,
        height: Int
    ): Boolean {
        var isSupport = false
        for (size: Size in supportedPreviewSizes) {
            if (size.width == width && size.height == height) {
                isSupport = true
                break
            }
        }
        return isSupport
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission(): Boolean {

        return when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                false
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_CODE
                )
                false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.toast("已授予摄像头权限")
                } else {
                    ToastUtils.toast("没有摄像头权限")
                }
            }
        }
    }
}