package com.xzl.project.minizhihu.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xzl.project.minizhihu.BaseFragment;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.DataBean.ArticleJsonData;
import com.xzl.project.minizhihu.DataBean.BannerJsonData;
import com.xzl.project.minizhihu.DataBean.FeedArticleData;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.utils.GlideImageLoader;
import com.xzl.project.minizhihu.utils.T;
import com.xzl.project.minizhihu.view.activity.TbsWebView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static android.support.constraint.Constraints.TAG;

public class ApplicationFragment extends BaseFragment implements OnBannerListener,BaseQuickAdapter.OnItemClickListener{
    private List<String> images,    titles,     urls;   //banner参数 图片，标题，URL
    private int page = 0;
    private String jsonString,newsdata;  //banner,news  json数据
    private Banner banner;  //banner控件
    private FloatingActionButton main_floating_action_btn;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private QuickAdapter quickAdapter;
    private List<FeedArticleData> datas;

    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setView(setLayout(inflater , container , R.layout.fragment_application) , this);
        //初始化界面
        initView(view);

        getHomePagerNewsData("http://www.wanandroid.com/article/list/"+page+"/json");

        setRefresh();

        urls = new ArrayList<>();
        titles = new ArrayList<>();
        images = new ArrayList<>();

        getData("http://www.wanandroid.com/banner/json");


        banner.setOnBannerListener(this);

        return view;
    }

    /**
     * 配置上拉加载 下拉刷新
     */
    private void setRefresh() {

        quickAdapter = new QuickAdapter(R.layout.item_pager,datas);
//        quickAdapter.replaceData(datas);

//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(quickAdapter);

        View header = LayoutInflater.from(getContext()).inflate(R.layout.applicaation_banner,mRecyclerView,false);
        banner = (Banner) header;
        quickAdapter.addHeaderView(banner);
        quickAdapter.openLoadAnimation();

        /** 设置屏幕滚动监听 设置回到顶部按钮是否隐藏 */
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                //当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    //判断是否滚动超过一屏
                    if (firstVisibleItemPosition == 0){
                        main_floating_action_btn.setVisibility(View.INVISIBLE);
                    }else {
                        main_floating_action_btn.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                        //拖动中
                        main_floating_action_btn.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        /**
         * 返回顶部
         */
        main_floating_action_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });

        //RecyclerView  Item 点击事件
        quickAdapter.setOnItemClickListener(this);
//        mRefreshLayout.setEnableAutoLoadMore(false);//在列表滚动到底部时自动加载更多
        /**
         * 下拉刷新监听
         */
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                datas.clear();
                page = 0;
                getHomePagerNewsData("http://www.wanandroid.com/article/list/"+page+"/json");
                quickAdapter.notifyDataSetChanged();
                refreshLayout.finishRefresh();
            }
        });



        /**
         * 上拉加载更多监听
         */
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page = page+1;
                Log.e(TAG, "onLoadMore: "+page);
                getHomePagerNewsData("http://www.wanandroid.com/article/list/"+page+"/json");
                quickAdapter.notifyDataSetChanged();
                refreshLayout.finishLoadMore();
            }
        });

    }

    private void initView(View view) {
//        banner = view.findViewById(R.id.banner);
        mRecyclerView = view.findViewById(R.id.application_reecyclerView);
        mRefreshLayout = view.findViewById(R.id.application_refreshLayout);
        datas = new ArrayList<>();
        main_floating_action_btn = view.findViewById(R.id.main_floating_action_btn);
    }

    /**
     * 设置首页轮播图
     */
    private void setBannerImage(List<String> images,List<String> titles){
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        //start
        banner.start();

    }

    /**
     * Banner get请求
     * @param url
     */
    private void getData(String url){
        //OKHTTPclient对象
        OkHttpClient client = new OkHttpClient();
        //构造Request
        //builder.get()代表是get请求，URL方法里面放的参数是一个网络地址
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //将request封装成call
        Call call = client.newCall(request);
        //执行call，这个方法是异步请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败调用
                Log.e(TAG, "onFailure: "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功调用

                //获取网络访问返回的字符串
                jsonString = response.body().string();
                setData(jsonString);
            }
        });
    }

    /***
     * banner点击事件监听
     * @param position
     */
    @Override
    public void OnBannerClick(int position) {
//        ToastUtil.showToast(getContext(),urls.get(position));
        Intent intent = new Intent(getContext(), TbsWebView.class);
        intent.putExtra("url",urls.get(position));
        intent.putExtra("title",titles.get(position));
        startActivity(intent);
    }


    /**
     * RecyclerView Item点击事件
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        ToastUtil.showToast(getContext(),"第几个？"+position);
        Intent intent = new Intent(getContext(),TbsWebView.class);
        intent.putExtra("url",datas.get(position).getLink());
        intent.putExtra("title",datas.get(position).getTitle());
        intent.putExtra("author",datas.get(position).getAuthor());
        startActivity(intent);
    }


    /**
     * 解析json数据
     * @param jsonString
     */
    private void setData(String jsonString){
        BannerJsonData bjd = new GetGsonData().GetAllData(jsonString);
        List<BannerJsonData.DataBean> dataBeans = bjd.getData();

        for (int i = 0;i < dataBeans.size();i++){
            BannerJsonData.DataBean dataBean = dataBeans.get(i);
            titles.add(dataBean.getTitle());
            images.add(dataBean.getImagePath());
            urls.add(dataBean.getUrl());
        }
//        Log.e(TAG, "setData: "+titles+"----"+images);
        Message message = new Message();
        message.what = 0;
        mBannerHandler.sendMessage(message);
    }

    /**
     * 更新界面
     */
    private Handler mBannerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                setBannerImage(images,titles);//设置Banner数据并显示
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume() {
        if (banner != null){
            banner.startAutoPlay();
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        if (banner != null){
            banner.stopAutoPlay();
        }
        super.onStop();
    }

    public class QuickAdapter extends BaseQuickAdapter<FeedArticleData,BaseViewHolder>{

        public QuickAdapter(@LayoutRes int layoutResId, @Nullable List<FeedArticleData> data) {
            super(layoutResId,data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final FeedArticleData item) {
            helper.setText(R.id.item_search_pager_author,item.getAuthor())
                    .setText(R.id.item_search_pager_chapterName,item.getSuperChapterName()+"/"+item.getChapterName())
                    .setText(R.id.item_search_pager_title,item.getTitle())
                    .setText(R.id.item_search_pager_niceDate,item.getNiceDate());

            helper.setOnClickListener(R.id.item_search_pager_chapterName, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T.s(getContext(),item.getChapterName());
                }
            });

//            helper.setOnClickListener(R.id.item_search_pager_like_iv, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.s(getContext(),"收藏成功");
//                    helper.setImageDrawable(R.id.item_search_pager_like_iv,getResources().getDrawable(R.drawable.icon_like));
//                }
//            });
        }
    }

    private void getHomePagerNewsData(String url){
        //OKHTTPclient对象
        OkHttpClient client = new OkHttpClient();
        //构造Request
        //builder.get()代表是get请求，URL方法里面放的参数是一个网络地址
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //将request封装成call
        Call call = client.newCall(request);
        //执行call，这个方法是异步请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败调用
                Log.e(TAG, "onFailure: "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功调用

                //获取网络访问返回的字符串
                newsdata = response.body().string();
                setNewsData(newsdata);
            }
        });
    }

    private void setNewsData(String newsdata) {

        FeedArticleData articleData;

        ArticleJsonData articleJsonData = new GetGsonData().GetNewsData(newsdata);
        ArticleJsonData.DataBean dataBean = articleJsonData.getData();
        List<ArticleJsonData.DataBean.DatasBean> datasBeans = dataBean.getDatas();

        for (int i = 0;i < datasBeans.size();i++){
            ArticleJsonData.DataBean.DatasBean datasBean = datasBeans.get(i);
            articleData = new FeedArticleData();
            articleData.setTitle(datasBean.getTitle());
            articleData.setAuthor(datasBean.getAuthor());
            articleData.setNiceDate(datasBean.getNiceDate());
            articleData.setChapterName(datasBean.getChapterName());
            articleData.setSuperChapterName(datasBean.getSuperChapterName());
            articleData.setLink(datasBean.getLink());
            datas.add(articleData);
        }

    }

    /**
     * RecyclerView 自定义分割线
     * @param drawableId
     * @return
     */
//    public RecyclerView.ItemDecoration getRecyclerViewDivider(@DrawableRes int drawableId){
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(getResources().getDrawable(drawableId));
//        return itemDecoration;
//    }
}
