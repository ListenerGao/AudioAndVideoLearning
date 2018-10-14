package com.listenergao.audioandvideolearning.activity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.listenergao.audioandvideolearning.R;
import com.linglong.lame.Lame;
import com.listenergao.audioandvideolearning.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 录音
 * create on 18/09/27
 *
 * @author listenergao
 */
public class RecordActivity extends BaseActivity implements MediaPlayer.OnCompletionListener {


    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.bt_start_record)
    Button mBtStartRecord;
    @BindView(R.id.bt_stop_record)
    Button mBtStopRecord;
    @BindView(R.id.bt_play_record)
    Button mBtPlayRecord;

    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;

    /**
     * 录音采集来源，此处是从麦克风采集
     */
    private static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /**
     * 设置采样频率
     */
    private static final int SIMPLE_RATE = 44100;
    /**
     * 设置单声道输入
     */
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 设置格式，安卓手机都支持是PCM16BIT
     */
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 录音文件保存的路径
     */
    private String mFilePath;
    private String mFileName;

    private AudioRecord mAudioRecord;
    private int mRecordBufSize = 1024 * 2;

    private File mRecordFile;

    private boolean isRecording = false;
    private FileOutputStream mFileOutputStream;
    private ExecutorService mExecutorService;
    private Lame mLame;
    private MediaPlayer mPlayer;

    private static Handler sHandler = new Handler();
    private Runnable mProgressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        createFile();
        mExecutorService = Executors.newSingleThreadExecutor();

        mLame = new Lame();
        mLame.init(1, 44100, 64);

        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d("gys", "duration = " + mPlayer.getDuration() + "currentPosition = " + mPlayer.getCurrentPosition());
                mSeekBar.setProgress(mPlayer.getCurrentPosition());
                sHandler.postDelayed(this, 1000);
            }
        };

    }

    @OnClick({R.id.bt_start_record, R.id.bt_stop_record, R.id.bt_play_record, R.id.bt_stop_play_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_start_record:
                startRecord();
                break;
            case R.id.bt_stop_record:
                stopRecord();
                break;
            case R.id.bt_play_record:
                playRecord(mFilePath + mFileName);
                break;
            case R.id.bt_stop_play_record:
                stopPlayRecord();
                break;
            default:
                break;
        }
    }

    /**
     * 创建存储录音文件的文件夹
     */
    private void createFile() {

        mFilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + this.getPackageName() + File.separator + "record" + File.separator;

        mRecordFile = new File(mFilePath);
        if (!mRecordFile.exists()) {
            mRecordFile.mkdirs();
            Log.d("gys", "创建文件夹-------");
        } else {
            Log.d("gys", "删除文件夹下所有文件-------");
            deleteAllFile(mRecordFile);
        }
    }

    /**
     * 删除文件或者删除目录下的所有文件
     *
     * @param path
     */
    private void deleteAllFile(File path) {
        if (path == null) {
            return;
        }
        //判断是文件还是目录，文件就直接删除
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            Log.d("gys", "file size = " + files.length);
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteAllFile(file);
                    Log.d("gys", "递归删除");
                } else if (file.isFile()) {
                    Log.d("gys", "删除文件名称：" + file.getName());
                    file.delete();
                }
            }
        } else {
            //文件直接删除
            path.delete();
        }
    }

    private void startRecord() {
        if (isRecording) {
            ToastUtils.toast("录音中...");
        } else {
            if (mExecutorService == null) {
                mExecutorService = Executors.newSingleThreadExecutor();
            }
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    record();
                }
            });
        }
    }

    /**
     * 录音
     */
    private void record() {
        try {

            mFileName = System.currentTimeMillis() + ".mp3";
            mFileOutputStream = new FileOutputStream(new File(mFilePath + mFileName));

//            byte[] data = new byte[mRecordBufSize];
            short[] data = new short[mRecordBufSize];

            int audioRecordBufferSize = AudioRecord.getMinBufferSize(SIMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
            Log.d("gys", "audioRecordBufferSize = " + audioRecordBufferSize);
            mAudioRecord = new AudioRecord(AUDIO_SOURCE, SIMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, audioRecordBufferSize);

            isRecording = true;
            mAudioRecord.startRecording();

            //写数据到文件
            while (isRecording) {
                int read = mAudioRecord.read(data, 0, mRecordBufSize);
                Log.d("gys", "read = " + read);
                if (read <= 50) {
                    continue;
                }

                if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    // 音频转码成mp3
                    byte[] encodeData = mLame.encode(data, read);
                    if (encodeData == null || encodeData.length < 10) {
                        break;
                    }
                    //保存到指定文件
                    mFileOutputStream.write(encodeData, 0, encodeData.length);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            isRecording = false;
        } finally {
            isRecording = false;
            try {
                mFileOutputStream.flush();
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        Log.d("gys", "停止录音");
        isRecording = false;
        if (mAudioRecord != null) {
            mAudioRecord.stop();
        } else {
            ToastUtils.toast("当前未在录音哦");
        }
    }

    /**
     * 释放AudioRecord
     */
    private void recordRelease() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        recordRelease();
        playerRelease();
        mLame.destroy();
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
    }

    /**
     * 播放录音文件
     *
     * @param filePath 录音文件路径
     */
    private void playRecord(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            ToastUtils.toast("未找到音频文件");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtils.toast("未找到音频文件");
            return;
        }

        mSeekBar.setVisibility(View.VISIBLE);

        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();

                mPlayer.setOnCompletionListener(this);
            }
            mPlayer.reset();
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();

            mSeekBar.setMax(mPlayer.getDuration());
            sHandler.post(mProgressRunnable);

        } catch (IOException e) {
            ToastUtils.toast("播放出错");
            sHandler.removeCallbacks(mProgressRunnable);
            stopPlayRecord();
            e.printStackTrace();
        }
    }


    /**
     * 释放MediaPlayer
     */
    private void playerRelease() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        sHandler.removeCallbacks(mProgressRunnable);
    }

    /**
     * 停止播放
     */
    private void stopPlayRecord() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                Log.d("gys", "stop......");
            }
        } else {
            ToastUtils.toast("当前未播放哦");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("gys", "播放完了...");
        stopPlayRecord();
    }
}
