package com.listenergao.audioandvideolearning.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.listenergao.audioandvideolearning.R;
import com.listenergao.audioandvideolearning.mode.CategoryBean;

import java.util.List;

public class CategoryAdapter extends BaseQuickAdapter<CategoryBean, BaseViewHolder> {

    public CategoryAdapter(@Nullable List<CategoryBean> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryBean item) {
        helper.setText(R.id.tv_name, item.categoryName);
    }
}
