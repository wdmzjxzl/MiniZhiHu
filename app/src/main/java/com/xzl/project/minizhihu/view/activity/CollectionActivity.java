package com.xzl.project.minizhihu.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xzl.project.minizhihu.DataBean.CollectionBean;
import com.xzl.project.minizhihu.DataBean.FeedArticleData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.utils.T;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CollectionActivity extends AppCompatActivity {

    @BindView(R.id.collection_toolbar_title_tv)
    TextView collectionToolbarTitleTv;
    @BindView(R.id.collection_toolbar)
    Toolbar collectionToolbar;
    @BindView(R.id.collection_recyclerview)
    RecyclerView collectionRecyclerview;
    @BindView(R.id.collection_refreshLayout)
    SmartRefreshLayout collectionRefreshLayout;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        collectionToolbarTitleTv.setText("收藏列表");
        QueryData();

        collectionRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                QueryData();
                adapter.notifyDataSetChanged();
                collectionRefreshLayout.finishRefresh();
            }
        });
    }

    private void QueryData() {
        BmobQuery<CollectionBean> query = new BmobQuery<>();
        query.setLimit(500);
        query.findObjects(new FindListener<CollectionBean>() {
            @Override
            public void done(List<CollectionBean> list, BmobException e) {
                if (e==null){
                    adapter = new BaseAdapter(R.layout.item_pager,list);
                    collectionRecyclerview.setLayoutManager(new LinearLayoutManager(CollectionActivity.this));
                    collectionRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
                    collectionRefreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
                    collectionRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
                    collectionRefreshLayout.setEnableAutoLoadMore(false);//在列表滚动到底部时自动加载更多
                    collectionRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
                    collectionRecyclerview.setAdapter(adapter);
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = new Intent(CollectionActivity.this,TbsWebView.class);
                            intent.putExtra("url",list.get(position).getLink());
                            intent.putExtra("title",list.get(position).getTitle());
                            intent.putExtra("author",list.get(position).getAuthor());
                            startActivity(intent);
                        }
                    });
                }else {
                    T.s(CollectionActivity.this,e.getMessage());
                }
            }
        });
    }


    private class BaseAdapter extends BaseQuickAdapter<CollectionBean,BaseViewHolder> {

        public BaseAdapter(int layoutResId, @Nullable List<CollectionBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CollectionBean item) {
            helper.setText(R.id.item_search_pager_author,item.getAuthor())
                    .setText(R.id.item_search_pager_title,item.getTitle());

        }
    }
}
