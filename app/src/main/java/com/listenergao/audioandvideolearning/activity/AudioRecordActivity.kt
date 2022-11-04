package com.listenergao.audioandvideolearning.activity

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.listenergao.audioandvideolearning.databinding.ActivityAudioRecordBinding
import com.listenergao.audioandvideolearning.utils.ToastUtils

/**
 * @description: 描述
 * @date: 2022/10/24 17:23
 * @author: ListenerGao
 */
class AudioRecordActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAudioRecordBinding

    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 1024
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAudioRecordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnAudioRecord.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v) {
            mBinding.btnAudioRecord -> {
                if (checkPermission()) {
                    audioRecord()
                }
            }
        }
    }

    private var mAudioRecord: AudioRecord? = null


    private fun audioRecord() {
        if (mAudioRecord == null) {
            /**
             * int audioSource 音频采集的输入源，可选值以常量形式定义在类 AudioSource （MediaRecorder 的一个内部类）中，常用的可选值如下：
             *      DEFAULT：代表使用默认的麦克风采集
             *      CAMCORDER：使用和摄像头同方向的麦克风采集
             *      VOICE_RECOGNITION：一般用于语音识别场景
             *      MIC：代表使用手机的主麦克风作为采集的输入源
             *      VOICE_COMMUNICATION：在 VOIP 场景下如果使用硬件 EAC 的话，可以设置这个参数
             *
             * int sampleRateInHz 用来指定采用多大的采样率来采集音频，现在用的最多的就是 44100 或者 48000 的采样率。
             *
             * int channelConfig 用于指定录音器采集几个声道的声音，可选值以常量的形式定义在 AudioFormat 类中，
             *      常用值包括：CHANNEL_IN_MONO 单声道采集和 CHANNEL_IN_STEREO 立体声采集。
             *
             * int audioFormat 采样格式，可选值以常量的形式定义在 AudioFormat 类中，常用的值包括：
             *      ENCODING_PCM_16BIT 使用 16 位或者 2 个字节来表示一个采样点，可以保证兼容大部分 Android 手机，一般都采用 16BIT。
             *      ENCODING_PCN_8BIT  使用 8 位或者 1 个字节来表示一个采样点。
             *
             * int bufferSizeInBytes 用于指定 AudioRecord 内部音频缓冲区的大小。具体的大小，不同的厂商会有不同的实现，
             *      这个音频缓冲区越小，录音的延时就会越小。在实际开发中，一般要通过 AudioRecord.getMinBufferSize()
             *      函数来计算要传入的 bufferSizeInBytes
             */

            val audioSource = AudioSource.DEFAULT
            val sampleRateInHz = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val bufferSizeInBytes =
                AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

            mAudioRecord = AudioRecord(
                audioSource,
                sampleRateInHz,
                channelConfig,
                audioFormat,
                bufferSizeInBytes
            )
        }

        // 检查当前 AudioRecord 状态
        if (mAudioRecord?.state == AudioRecord.STATE_INITIALIZED) {
            mAudioRecord?.startRecording()

//            mAudioRecord?.read()
        } else {
            showToast("AudioRecord 初始化失败...")
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission(): Boolean {

        return when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                false
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
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
            REQUEST_RECORD_AUDIO_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.toast("已授予录音权限")
                } else {
                    ToastUtils.toast("没有录音权限")
                }
            }
        }
    }

}