package com.listenergao.audioandvideolearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.listenergao.audioandvideolearning.activity.BaseActivity;
import com.listenergao.audioandvideolearning.adapter.CategoryAdapter;
import com.listenergao.audioandvideolearning.mode.CategoryBean;
import com.listenergao.audioandvideolearning.utils.CommonConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<CategoryBean> mCategorys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        mCategorys = new ArrayList<>();

        mCategorys.add(new CategoryBean("通过三种方式绘制图片", CommonConfig.DRAW_PICTURE));

        CategoryAdapter mAdapter = new CategoryAdapter(mCategorys);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Toast.makeText(this, "通过三种方式绘制图片",Toast.LENGTH_SHORT).show();
    }
}
