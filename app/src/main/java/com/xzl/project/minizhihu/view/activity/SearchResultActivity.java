package com.xzl.project.minizhihu.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.xzl.project.minizhihu.DataBean.FeedArticleData;
import com.xzl.project.minizhihu.DataBean.SearchResultDataBean;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.utils.StatusBarUtil;
import com.xzl.project.minizhihu.utils.T;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.search_result_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.search_result_toolbar_title)
    TextView mTitle;
    @BindView(R.id.search_result_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.search_result_refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int position = 0;
    Intent intent;
    List<FeedArticleData> listData;
    private BaseAdapter baseAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcch_result);
        ButterKnife.bind(this);

        init();

        new Thread(runnable).start();

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                position = position+1;
                new Thread(runnable).start();
                baseAdapter.notifyDataSetChanged();
                refreshLayout.finishLoadMore();
            }
        });
    }

    private void init() {
        intent = getIntent();
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mToolbar);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mTitle.setText(intent.getStringExtra("searchText"));
        mToolbar.setNavigationOnClickListener(this);

        listData = new ArrayList<>();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getAllData("http://www.wanandroid.com/article/query/"+position+"/json");
        }
    };

    private void getAllData(String url){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("k",intent.getStringExtra("searchText"))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
//                    Log.e("=====", "getSearchResultData: "+response.body().string());
                Message message = new Message();
                Bundle data = new Bundle();
                data.putString("value",response.body().string());
                message.setData(data);
                message.what = 0x11;
                handler.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x11:
                    Bundle data = msg.getData();
                    String val = data.getString("value");
                    setRecyclerViewData(val);
                    break;
                case 0x12:
                    baseAdapter = new BaseAdapter(R.layout.item_pager,listData);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
                    refreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
                    refreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
                    refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
                    refreshLayout.setEnableAutoLoadMore(false);//在列表滚动到底部时自动加载更多
                    refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
                    mRecyclerView.setAdapter(baseAdapter);
                    baseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Intent intent = new Intent(SearchResultActivity.this,TbsWebView.class);
                            intent.putExtra("url",listData.get(position).getLink());
                            intent.putExtra("title",listData.get(position).getTitle());
                            intent.putExtra("author",listData.get(position).getAuthor());
                            startActivity(intent);
                        }
                    });
                    break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        finish();
    }

    private void setRecyclerViewData(String json){
        FeedArticleData feedArticleData;
        SearchResultDataBean searchResultDataBean = new GetGsonData().getSeatchResultData(json);
        SearchResultDataBean.DataBean dataBean = searchResultDataBean.getData();
        List<SearchResultDataBean.DataBean.DatasBean> datasBeans = dataBean.getDatas();
        for ( int i=0;i<datasBeans.size();i++){
            SearchResultDataBean.DataBean.DatasBean datasBean = datasBeans.get(i);
            feedArticleData = new FeedArticleData();

            feedArticleData.setTitle(String.valueOf(Html.fromHtml(datasBean.getTitle())));
            feedArticleData.setAuthor(datasBean.getAuthor());
            feedArticleData.setNiceDate(datasBean.getNiceDate());
            feedArticleData.setChapterName(datasBean.getChapterName());
            feedArticleData.setSuperChapterName(datasBean.getSuperChapterName());
            feedArticleData.setLink(datasBean.getLink());
            listData.add(feedArticleData);
        }
        Message message = new Message();
        message.what = 0x12;
        handler.sendMessage(message);
    }

    private class BaseAdapter extends BaseQuickAdapter<FeedArticleData,BaseViewHolder>{

        public BaseAdapter(int layoutResId, @Nullable List<FeedArticleData> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, FeedArticleData item) {
            helper.setText(R.id.item_search_pager_author,item.getAuthor())
                    .setText(R.id.item_search_pager_chapterName,item.getSuperChapterName()+"/"+item.getChapterName())
                    .setText(R.id.item_search_pager_title,item.getTitle())
                    .setText(R.id.item_search_pager_niceDate,item.getNiceDate());

            if (item.getSuperChapterName().equals("") && item.getChapterName().equals("")){
                helper.setText(R.id.item_search_pager_chapterName,"");
            }

//            helper.setOnClickListener(R.id.item_search_pager_like_iv, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.s(SearchResultActivity.this,"收藏成功");
//                    helper.setImageDrawable(R.id.item_search_pager_like_iv,getResources().getDrawable(R.drawable.icon_like));
//                }
//            });
        }
    }
}
