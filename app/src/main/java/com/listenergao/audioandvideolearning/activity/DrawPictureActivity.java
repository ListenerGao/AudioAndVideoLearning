package com.listenergao.audioandvideolearning.activity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.listenergao.audioandvideolearning.R;
import com.listenergao.audioandvideolearning.utils.ToastUtils;
import com.listenergao.audioandvideolearning.view.CustomImageView;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通过三种方式绘制图片
 * create on 18/09/16
 *
 * @author listenergao
 */
public class DrawPictureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @BindView(R.id.btn_one)
    Button mBtnOne;
    @BindView(R.id.btn_two)
    Button mBtnTwo;
    @BindView(R.id.btn_three)
    Button mBtnThree;
    @BindView(R.id.tv_introduction)
    TextView mTvIntroduction;
    @BindView(R.id.iv_show)
    ImageView mIvShow;
    @BindView(R.id.custom_image_view)
    CustomImageView mCustomImageView;
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_picture);
        ButterKnife.bind(this);

        drawPictureWithSurfaceView();

    }

    @OnClick({R.id.btn_one, R.id.btn_two, R.id.btn_three})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_one:
                mIvShow.setVisibility(View.VISIBLE);
                mCustomImageView.setVisibility(View.GONE);
                mSurfaceView.setVisibility(View.GONE);
                mTvIntroduction.setText("通过ImageView绘制图片");
                drawPictureWithImageView();
                break;
            case R.id.btn_two:
                mIvShow.setVisibility(View.GONE);
                mCustomImageView.setVisibility(View.VISIBLE);
                mSurfaceView.setVisibility(View.GONE);
                mTvIntroduction.setText("通过自定义View的方法绘制图片");
                mCustomImageView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

                break;
            case R.id.btn_three:
                mIvShow.setVisibility(View.GONE);
                mCustomImageView.setVisibility(View.GONE);
                mSurfaceView.setVisibility(View.VISIBLE);
                mTvIntroduction.setText("通过SurfaceView绘制图片");
                drawPictureWithSurfaceView();

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
            mIvShow.setImageBitmap(bitmap);
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
        SurfaceHolder holder = mSurfaceView.getHolder();
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
