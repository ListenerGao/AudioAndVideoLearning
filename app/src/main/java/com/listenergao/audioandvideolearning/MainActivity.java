package com.listenergao.audioandvideolearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.listenergao.audioandvideolearning.activity.BaseActivity;
import com.listenergao.audioandvideolearning.activity.DrawPictureActivity;
import com.listenergao.audioandvideolearning.activity.RecordActivity;
import com.listenergao.audioandvideolearning.adapter.CategoryAdapter;
import com.listenergao.audioandvideolearning.databinding.ActivityMainBinding;
import com.listenergao.audioandvideolearning.mode.CategoryBean;
import com.listenergao.audioandvideolearning.utils.CommonConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * @author listenergao
 */
public class MainActivity extends BaseActivity implements OnItemClickListener {

    private ActivityMainBinding mBinding;
    private List<CategoryBean> mCategorys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initData();
    }

    private void initData() {
        mCategorys = new ArrayList<>();

        mCategorys.add(new CategoryBean("通过三种方式绘制图片", CommonConfig.DRAW_PICTURE));
        mCategorys.add(new CategoryBean("通过AudioRecord录音", CommonConfig.RECORD));

        CategoryAdapter mAdapter = new CategoryAdapter(mCategorys);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CategoryBean item = (CategoryBean) adapter.getItem(position);

        if (item == null) {
            return;
        }
        switch (item.tag) {
            case CommonConfig.DRAW_PICTURE:
                startActivity(new Intent(MainActivity.this, DrawPictureActivity.class));
                break;

            case CommonConfig.RECORD:
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
                break;
            default:
                break;
        }
    }
}
