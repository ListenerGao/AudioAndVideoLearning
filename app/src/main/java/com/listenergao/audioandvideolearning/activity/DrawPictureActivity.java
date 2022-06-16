package com.listenergao.audioandvideolearning.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.listenergao.audioandvideolearning.R;
import com.listenergao.audioandvideolearning.databinding.ActivityDrawPictureBinding;
import com.listenergao.audioandvideolearning.utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;


/**
 * 通过三种方式绘制图片
 * create on 18/09/16
 *
 * @author listenergao
 */
public class DrawPictureActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {


    private ActivityDrawPictureBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDrawPictureBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setClickListener();

        drawPictureWithSurfaceView();

    }

    private void setClickListener() {
        mBinding.btnOne.setOnClickListener(this);
        mBinding.btnTwo.setOnClickListener(this);
        mBinding.btnThree.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_one:
                mBinding.ivShow.setVisibility(View.VISIBLE);
                mBinding.customImageView.setVisibility(View.GONE);
                mBinding.surfaceView.setVisibility(View.GONE);
                mBinding.tvIntroduction.setText("通过ImageView绘制图片");
                drawPictureWithImageView();
                break;
            case R.id.btn_two:
                mBinding.ivShow.setVisibility(View.GONE);
                mBinding.customImageView.setVisibility(View.VISIBLE);
                mBinding.surfaceView.setVisibility(View.GONE);
                mBinding.tvIntroduction.setText("通过自定义View的方法绘制图片");
                mBinding.customImageView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

                break;
            case R.id.btn_three:
                mBinding.ivShow.setVisibility(View.GONE);
                mBinding.customImageView.setVisibility(View.GONE);
                mBinding.surfaceView.setVisibility(View.VISIBLE);
                mBinding.tvIntroduction.setText("通过SurfaceView绘制图片");
                drawPictureWithSurfaceView();

                break;
            default:
                break;
        }
    }


    /**
     * 通过ImageView绘制图片
     */
    private void drawPictureWithImageView() {
        Log.d("gys", "drawPictureWithImageView");
        Bitmap bitmap = getBitmapFromAssetsFile(this, "picture.jpg");
        if (bitmap == null) {
            ToastUtils.toast("获取图片失败");
        } else {
            mBinding.ivShow.setImageBitmap(bitmap);
        }
    }

    private Bitmap getBitmapFromAssetsFile(Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 使用SurfaceView绘制照片
     */
    private void drawPictureWithSurfaceView() {
        Log.d("gys", "drawPictureWithSurfaceView");
        SurfaceHolder holder = mBinding.surfaceView.getHolder();
        holder.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("gys", "surfaceCreated");
        if (surfaceHolder != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            //获取图片bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
            //先锁定当前SurfaceView的画布
            Canvas canvas = surfaceHolder.lockCanvas();
            //执行绘制操作
            canvas.drawBitmap(bitmap, 0, 0, paint);
            //解除锁定，并显示在界面上
            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d("gys", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("gys", "surfaceDestroyed");
    }


}
