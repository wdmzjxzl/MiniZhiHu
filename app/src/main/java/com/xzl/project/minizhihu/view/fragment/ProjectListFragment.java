package com.xzl.project.minizhihu.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xzl.project.minizhihu.BaseFragment;
import com.xzl.project.minizhihu.DataBean.ProjectDataBean;
import com.xzl.project.minizhihu.DataBean.ProjectDeatilBean;
import com.xzl.project.minizhihu.DataBean.ProjectListData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.view.activity.TbsWebView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener{
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private int positionID;
    private int pager = 0;
    private String url ="http://www.wanandroid.com/project/list/"+pager+"/json?cid=";

    private OkHttpClient client;
    private Request.Builder builder;
    private Request request;
    private Call call;

    List<ProjectListData> projectListData;
    ProjectAdapter adapter;

    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setView(setLayout(inflater,container, R.layout.fragment_list_project),this);
        initView(view);

        getData(url,positionID);
        setRefresh();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProjectAdapter(R.layout.item_project_list,projectListData);
        adapter.openLoadAnimation(BaseQuickAdapter.FOOTER_VIEW);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    private void initView(View view) {
        smartRefreshLayout = view.findViewById(R.id.project_list_refreshLayout);
        recyclerView = view.findViewById(R.id.project_list_recyclerview);
        Bundle bundle = getArguments();
        positionID = bundle.getInt("id");
        projectListData = new ArrayList<>();

    }

    private void getData(String url,int positionID) {
        //OKHTTPclient对象
        client = new OkHttpClient();
        //构造Request
        //builder.get()代表是get请求，URL方法里面放的参数是一个网络地址
        builder = new Request.Builder();
        request = builder.get().url(url+positionID).build();
        //将request封装成call
        call = client.newCall(request);
        //执行call，这个方法是异步请求数据
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败调用
                Log.e("=====", "onFailure: "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功调用

                //获取网络访问返回的字符串
//                Log.e("====", "onResponse: "+response.body().string());
                SetAllData(response.body().string());
            }
        });
    }

    private void SetAllData(String jsonData) {
        ProjectListData listData;
        ProjectDeatilBean deatilBean = new GetGsonData().getProjectDeatilBean(jsonData);
        ProjectDeatilBean.DataBean dataBean = deatilBean.getData();
        List<ProjectDeatilBean.DataBean.DatasBean> datasBeanList = dataBean.getDatas();
        for (int i=0;i<datasBeanList.size();i++){
            ProjectDeatilBean.DataBean.DatasBean datasBean = datasBeanList.get(i);
            listData = new ProjectListData();
            listData.setEnvelopePic(datasBean.getEnvelopePic());
            listData.setAuthor(datasBean.getAuthor());
            listData.setDesc(datasBean.getDesc());
            listData.setLink(datasBean.getLink());
            listData.setNiceDate(datasBean.getNiceDate());
            listData.setProjectLink(datasBean.getProjectLink());
            listData.setTitle(datasBean.getTitle());
            projectListData.add(listData);
        }
//        Message message = new Message();
//        message.what = 0x11;
//        handler.sendMessage(message);
    }

//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 0x11:
//
//                    break;
//            }
//        }
//    };


    private void setRefresh() {
        smartRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
        smartRefreshLayout.setEnableOverScrollDrag(false);//是否启用越界拖动（仿苹果效果）1.0.4
        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//取消内容不满一页时开启上拉加载功能
        smartRefreshLayout.setEnableAutoLoadMore(false);//在列表滚动到底部时自动加载更多
        smartRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                projectListData.clear();
                pager = 0;
                getData("http://www.wanandroid.com/article/list/"+pager+"/json?cid=",positionID);
//                Log.e(TAG, "onLoadMore: "+page);

                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
                smartRefreshLayout.finishRefresh();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pager = pager+1;
                getData("http://www.wanandroid.com/article/list/"+pager+"/json?cid=",positionID);
//                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                smartRefreshLayout.finishLoadMore();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getContext(),TbsWebView.class);
        intent.putExtra("url",projectListData.get(position).getLink());
        intent.putExtra("title",projectListData.get(position).getTitle());
        intent.putExtra("author",projectListData.get(position).getAuthor());
        startActivity(intent);
    }

    private class ProjectAdapter extends BaseQuickAdapter<ProjectListData,BaseViewHolder>{

        public ProjectAdapter(int layoutResId, @Nullable List<ProjectListData> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProjectListData item) {
            helper.setText(R.id.item_project_list_title_tv,item.getTitle())
                    .setText(R.id.item_project_list_content_tv,item.getDesc())
                    .setText(R.id.item_project_list_time_tv,item.getNiceDate())
                    .setText(R.id.item_project_list_author_tv,item.getAuthor());
            ImageView imageView = helper.getView(R.id.item_project_list_iv);
            Glide.with(getContext()).load(item.getEnvelopePic()).into(imageView);
        }
    }
}
