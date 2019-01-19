package com.xzl.project.minizhihu.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.xzl.project.minizhihu.DataBean.DeatilBean;
import com.xzl.project.minizhihu.DataBean.KnowledgeDeatilBean;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.utils.T;
import com.xzl.project.minizhihu.view.activity.TbsWebView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

@SuppressLint("ValidFragment")
public class KnowledgeDeatilFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener{
    View view;
    private int positionId;
    private static final String TAG = "=========";

    private OkHttpClient client;
    private Request.Builder builder;
    private Request request;
    private Call call;
    private List<DeatilBean> articledata;
    private RecyclerView mRecyclerView;
    private KnowledgeDeatilAdapter adapter;
    private SmartRefreshLayout mRefreshLayout;
    private int page = 0;
    private String url = "http://www.wanandroid.com/article/list/"+page+"/json?cid=";

//    public static KnowledgeDeatilFragment getInstant(int id){
//        KnowledgeDeatilFragment mf = new KnowledgeDeatilFragment();
//        mf.positionId = id;
//        return mf;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = setView(setLayout(inflater , container , R.layout.fragment_knowledge_detail) , this);
        initView(view);

        GetBaseData(url,positionId);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
        mRefreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
        mRefreshLayout.setEnableAutoLoadMore(false);//在列表滚动到底部时自动加载更多
        mRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        adapter = new KnowledgeDeatilAdapter(R.layout.item_pager,articledata);
        adapter.openLoadAnimation(BaseQuickAdapter.FOOTER_VIEW);
        adapter.setOnItemClickListener(this);

        setRefresh();
        return view;
    }

    private void setRefresh() {
        mRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
        mRefreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
        mRefreshLayout.setEnableAutoLoadMore(false);//在列表滚动到底部时自动加载更多
        mRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        /**
         * 下拉刷新监听
         */
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                articledata.clear();
                page = 0;
                GetBaseData("http://www.wanandroid.com/article/list/"+page+"/json?cid=",positionId);
//                Log.e(TAG, "onLoadMore: "+page);
//                adapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(adapter);
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
                GetBaseData("http://www.wanandroid.com/article/list/"+page+"/json?cid=",positionId);
                adapter.notifyDataSetChanged();
                mRefreshLayout.finishLoadMore();
            }
        });
    }


    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.knowledge_detail_recyclerView);
        articledata = new ArrayList<>();
        Bundle bundle = getArguments();
        positionId = bundle.getInt("positionID");
        mRefreshLayout = view.findViewById(R.id.knowledge_detail_refreshLayout);
    }

    private void GetBaseData(String url,int positionId){

        //OKHTTPclient对象
        client = new OkHttpClient();
        //构造Request
        //builder.get()代表是get请求，URL方法里面放的参数是一个网络地址
        builder = new Request.Builder();
        request = builder.get().url(url+positionId).build();
        //将request封装成call
        call = client.newCall(request);
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
//                Log.e(TAG, "onResponse: "+response.body().string());
                SetAllData(response.body().string());
            }
        });
    }

    private void SetAllData(String json){
        DeatilBean articleData;
        KnowledgeDeatilBean deatilData = new GetGsonData().getDeatilData(json);
        KnowledgeDeatilBean.DataBean dataBean = deatilData.getData();
        List<KnowledgeDeatilBean.DataBean.DatasBean> datasBeans = dataBean.getDatas();

        for (int i = 0;i < datasBeans.size();i++){
            KnowledgeDeatilBean.DataBean.DatasBean datasBean = datasBeans.get(i);
            articleData= new DeatilBean();
            articleData.setTitle(datasBean.getTitle());
            articleData.setAuthor(datasBean.getAuthor());
            articleData.setNiceDate(datasBean.getNiceDate());
            articleData.setChapterName(datasBean.getChapterName());
            articleData.setSuperChapterName(datasBean.getSuperChapterName());
            articleData.setLink(datasBean.getLink());
            articledata.add(articleData);
        }
        Message message = new Message();
        message.what = 0x11;
        recyclerHandler.sendMessage(message);
    }

    /**
     * 更新界面
     */
    @SuppressLint("HandlerLeak")
    private Handler recyclerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x11:
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getContext(),TbsWebView.class);
        intent.putExtra("url",articledata.get(position).getLink());
        intent.putExtra("title",articledata.get(position).getTitle());
        intent.putExtra("author",articledata.get(position).getAuthor());
        startActivity(intent);
    }

    private class KnowledgeDeatilAdapter extends BaseQuickAdapter<DeatilBean,BaseViewHolder>{

        public KnowledgeDeatilAdapter(int layoutResId, @Nullable List<DeatilBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DeatilBean item) {
            helper.setText(R.id.item_search_pager_author,item.getAuthor())
                    .setText(R.id.item_search_pager_chapterName,item.getSuperChapterName()+"/"+item.getChapterName())
                    .setText(R.id.item_search_pager_title,item.getTitle())
                    .setText(R.id.item_search_pager_niceDate,item.getNiceDate());

//            helper.setOnClickListener(R.id.item_search_pager_chapterName, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.s(getContext(),item.getChapterName());
//                }
//            });

//            helper.setOnClickListener(R.id.item_search_pager_like_iv, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.s(getContext(),"收藏成功");
//                    helper.setImageDrawable(R.id.item_search_pager_like_iv,getResources().getDrawable(R.drawable.icon_like));
//                }
//            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
