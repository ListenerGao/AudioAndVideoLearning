package com.listenergao.audioandvideolearning.activity

import android.media.AudioFormat
import com.listenergao.audioandvideolearning.utils.ToastUtils.toast
import android.media.MediaPlayer.OnCompletionListener
import android.media.AudioRecord
import com.linglong.lame.Lame
import android.media.MediaPlayer
import android.os.Bundle
import com.listenergao.audioandvideolearning.R
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.listenergao.audioandvideolearning.databinding.ActivityRecordBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 录音
 * create on 18/09/27
 *
 * @author listenergao
 */
class RecordActivity : BaseActivity(), OnCompletionListener, View.OnClickListener {
    /**
     * 录音文件保存的路径
     */
    private var mFilePath: String? = null
    private var mFileName: String? = null
    private var mAudioRecord: AudioRecord? = null
    private val mRecordBufSize = 1024 * 2
    private var mRecordFile: File? = null
    private var isRecording = false
    private var mFileOutputStream: FileOutputStream? = null
    private var mExecutorService: ExecutorService? = null
    private var mLame: Lame? = null
    private var mPlayer: MediaPlayer? = null
    private var mProgressRunnable: Runnable? = null
    private var mBinding: ActivityRecordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRecordBinding.inflate(
            layoutInflater
        )
        setContentView(mBinding!!.root)
        setClickListener()
        createFile()
        mExecutorService = Executors.newSingleThreadExecutor()
        mLame = Lame()
        mLame!!.init(1, 44100, 64)
        mProgressRunnable = object : Runnable {
            override fun run() {
                Log.d(
                    "gys",
                    "duration = " + mPlayer!!.duration + "currentPosition = " + mPlayer!!.currentPosition
                )
                mBinding!!.seekBar.progress = mPlayer!!.currentPosition
                sHandler.postDelayed(this, 1000)
            }
        }
    }

    private fun setClickListener() {
        mBinding!!.btStartRecord.setOnClickListener(this)
        mBinding!!.btStopRecord.setOnClickListener(this)
        mBinding!!.btStopPlayRecord.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_start_record -> startRecord()
            R.id.bt_stop_record -> stopRecord()
            R.id.bt_play_record -> playRecord(mFilePath + mFileName)
            R.id.bt_stop_play_record -> stopPlayRecord()
            else -> {}
        }
    }

    /**
     * 创建存储录音文件的文件夹
     */
    private fun createFile() {
        mFilePath =
            Environment.getExternalStorageDirectory().path + File.separator + this.packageName + File.separator + "record" + File.separator
        mRecordFile = File(mFilePath)
        if (!mRecordFile!!.exists()) {
            mRecordFile!!.mkdirs()
            Log.d("gys", "创建文件夹-------")
        } else {
            Log.d("gys", "删除文件夹下所有文件-------")
            deleteAllFile(mRecordFile)
        }
    }

    /**
     * 删除文件或者删除目录下的所有文件
     *
     * @param path
     */
    private fun deleteAllFile(path: File?) {
        if (path == null) {
            return
        }
        //判断是文件还是目录，文件就直接删除
        if (path.isDirectory) {
            val files = path.listFiles()
            Log.d("gys", "file size = " + files.size)
            for (file in files) {
                if (file.isDirectory) {
                    deleteAllFile(file)
                    Log.d("gys", "递归删除")
                } else if (file.isFile) {
                    Log.d("gys", "删除文件名称：" + file.name)
                    file.delete()
                }
            }
        } else {
            //文件直接删除
            path.delete()
        }
    }

    private fun startRecord() {
        if (isRecording) {
            toast("录音中...")
        } else {
            if (mExecutorService == null) {
                mExecutorService = Executors.newSingleThreadExecutor()
            }
            mExecutorService!!.execute { record() }
        }
    }

    /**
     * 录音
     */
    private fun record() {
        try {
            mFileName = System.currentTimeMillis().toString() + ".mp3"
            mFileOutputStream = FileOutputStream(File(mFilePath + mFileName))

//            byte[] data = new byte[mRecordBufSize];
            val data = ShortArray(mRecordBufSize)
            val audioRecordBufferSize =
                AudioRecord.getMinBufferSize(SIMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            Log.d("gys", "audioRecordBufferSize = $audioRecordBufferSize")
            mAudioRecord = AudioRecord(
                AUDIO_SOURCE,
                SIMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                audioRecordBufferSize
            )
            isRecording = true
            mAudioRecord!!.startRecording()

            //写数据到文件
            while (isRecording) {
                val read = mAudioRecord!!.read(data, 0, mRecordBufSize)
                Log.d("gys", "read = $read")
                if (read <= 50) {
                    continue
                }
                if (mAudioRecord!!.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    // 音频转码成mp3
                    val encodeData = mLame!!.encode(data, read)
                    if (encodeData == null || encodeData.size < 10) {
                        break
                    }
                    //保存到指定文件
                    mFileOutputStream!!.write(encodeData, 0, encodeData.size)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            isRecording = false
        } finally {
            isRecording = false
            try {
                mFileOutputStream!!.flush()
                mFileOutputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 停止录音
     */
    private fun stopRecord() {
        Log.d("gys", "停止录音")
        isRecording = false
        if (mAudioRecord != null) {
            mAudioRecord!!.stop()
        } else {
            toast("当前未在录音哦")
        }
    }

    /**
     * 释放AudioRecord
     */
    private fun recordRelease() {
        if (mAudioRecord != null) {
            mAudioRecord!!.release()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        recordRelease()
        playerRelease()
        mLame!!.destroy()
        if (mExecutorService != null) {
            mExecutorService!!.shutdown()
        }
    }

    /**
     * 播放录音文件
     *
     * @param filePath 录音文件路径
     */
    private fun playRecord(filePath: String) {
        if (TextUtils.isEmpty(filePath)) {
            toast("未找到音频文件")
            return
        }
        val file = File(filePath)
        if (!file.exists()) {
            toast("未找到音频文件")
            return
        }
        mBinding!!.seekBar.visibility = View.VISIBLE
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer()
                mPlayer!!.setOnCompletionListener(this)
            }
            mPlayer!!.reset()
            mPlayer!!.setDataSource(filePath)
            mPlayer!!.prepare()
            mPlayer!!.start()
            mBinding!!.seekBar.max = mPlayer!!.duration
            sHandler.post(mProgressRunnable!!)
        } catch (e: IOException) {
            toast("播放出错")
            sHandler.removeCallbacks(mProgressRunnable!!)
            stopPlayRecord()
            e.printStackTrace()
        }
    }

    /**
     * 释放MediaPlayer
     */
    private fun playerRelease() {
        if (mPlayer != null) {
            mPlayer!!.release()
        }
        sHandler.removeCallbacks(mProgressRunnable!!)
    }

    /**
     * 停止播放
     */
    private fun stopPlayRecord() {
        if (mPlayer != null) {
            if (mPlayer!!.isPlaying) {
                mPlayer!!.stop()
                Log.d("gys", "stop......")
            }
        } else {
            toast("当前未播放哦")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        Log.d("gys", "播放完了...")
        stopPlayRecord()
    }

    companion object {
        /**
         * 录音采集来源，此处是从麦克风采集
         */
        private const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC

        /**
         * 设置采样频率
         */
        private const val SIMPLE_RATE = 44100

        /**
         * 设置单声道输入
         */
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO

        /**
         * 设置格式，安卓手机都支持是PCM16BIT
         */
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val sHandler = Handler()
    }
}