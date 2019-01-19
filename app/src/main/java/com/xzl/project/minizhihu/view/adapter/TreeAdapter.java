package com.xzl.project.minizhihu.view.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzl.project.minizhihu.DataBean.StructureData;
import com.xzl.project.minizhihu.DataBean.TreeJsonData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.utils.CommonUtils;

import java.util.List;

public class TreeAdapter extends BaseQuickAdapter<StructureData,BaseViewHolder> {

    public TreeAdapter(int layoutResId, @Nullable List<StructureData> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StructureData item) {
        if (item.getName() == null){
            return;
        }
        helper.setText(R.id.item_knowledge_hierarchy_title,item.getName());
        helper.setTextColor(R.id.item_knowledge_hierarchy_title, CommonUtils.randomColor());
        if (item.getChildren() == null){
            return;
        }

        StringBuffer content = new StringBuffer();
        for (TreeJsonData.DataBean.ChildrenBean data : item.getChildren()){
            content.append(data.getName()).append("     ");
        }
        helper.setText(R.id.item_knowledge_hierarchy_content,content.toString());
    }
}
