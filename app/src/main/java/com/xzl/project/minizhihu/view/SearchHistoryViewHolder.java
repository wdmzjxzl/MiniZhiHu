package com.xzl.project.minizhihu.view;

import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzl.project.minizhihu.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author quchao
 * @date 2018/3/23
 */

public class SearchHistoryViewHolder extends BaseViewHolder {

    @BindView(R.id.item_search_history_tv)
    TextView mSearchHistoryTv;

    public SearchHistoryViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
