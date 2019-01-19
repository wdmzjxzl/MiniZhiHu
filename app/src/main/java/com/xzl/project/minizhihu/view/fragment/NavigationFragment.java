package com.xzl.project.minizhihu.view.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzl.project.minizhihu.BaseFragment;
import com.xzl.project.minizhihu.DataBean.FeedArticleData;
import com.xzl.project.minizhihu.DataBean.NavigationDataList;
import com.xzl.project.minizhihu.DataBean.NavigationJsonData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.utils.CommonUtils;
import com.xzl.project.minizhihu.utils.T;
import com.xzl.project.minizhihu.view.activity.TbsWebView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.ITabView;
import q.rorbin.verticaltablayout.widget.QTabView;
import q.rorbin.verticaltablayout.widget.TabView;

import static android.support.constraint.Constraints.TAG;

public class NavigationFragment extends BaseFragment {
//    private VerticalTabLayout tabLayout;
//    private View fenge;
    private RecyclerView mRecyclerView;
    private List<NavigationDataList> dataLists;
    List<String> titles;
    NavigationAdapter adapter;

    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setView(setLayout(inflater , container , R.layout.fragment_navigation) , this);
        initView(view);
        GetNavigationData();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NavigationAdapter(R.layout.item_navigation_list_page,dataLists);
        mRecyclerView.setAdapter(adapter);

//        tabLayout.setOnClickListener(this);

        return view;
    }


    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.navigation_RecyclerView);
//        fenge = view.findViewById(R.id.navigation_divider);
//        tabLayout = view.findViewById(R.id.navigation_tab_layout);
        dataLists = new ArrayList<>();
    }

    private void GetNavigationData() {
        //OKHTTPclient对象
        OkHttpClient client = new OkHttpClient();
        //构造Request
        //builder.get()代表是get请求，URL方法里面放的参数是一个网络地址
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url("http://www.wanandroid.com/navi/json").build();
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
                setNavigationsData(response.body().string());
            }
        });
    }

    private void setNavigationsData(String json) {
        NavigationDataList navigationDataList;
        NavigationJsonData jsonData = new GetGsonData().GetNavigationData(json);
        List<NavigationJsonData.DataBean> dataBean = jsonData.getData();
        titles = new ArrayList<>();
        for (int i=0;i<dataBean.size();i++){
            NavigationJsonData.DataBean dataBeans = dataBean.get(i);
            navigationDataList = new NavigationDataList();
            navigationDataList.setName(dataBeans.getName());
            titles.add(dataBeans.getName());
            navigationDataList.setArticlesBeans(dataBeans.getArticles());
            List<NavigationJsonData.DataBean.ArticlesBean> articlesBeanList = dataBeans.getArticles();
            for (int z=0;z<articlesBeanList.size();z++){
                NavigationJsonData.DataBean.ArticlesBean articlesBean = articlesBeanList.get(z);
                navigationDataList.setLink(articlesBean.getLink());
                navigationDataList.setTitle(articlesBean.getTitle());
            }
            dataLists.add(navigationDataList);
        }
        Message message = new Message();
        message.what = 0x12;
        handler.sendMessage(message);

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x12:
//                    tabLayout.setTabAdapter(new MyPagerAdapter(titles));
                    break;
            }
        }
    };

    private class MyPagerAdapter implements TabAdapter{
        List<String> titleString;

        public MyPagerAdapter(List<String> titles){
            this.titleString = titles;
        }

        @Override
        public int getCount() {
            return titleString.size();
        }

        @Override
        public ITabView.TabBadge getBadge(int position) {
            return null;
        }

        @Override
        public ITabView.TabIcon getIcon(int position) {
            return null;
        }

        @Override
        public ITabView.TabTitle getTitle(int position) {
            return new TabView.TabTitle.Builder()
                    .setContent(titleString.get(position))
                    .setTextColor(ContextCompat.getColor(getContext(), R.color.deep_red),
                            ContextCompat.getColor(getContext(),R.color.grey_search))
                    .build();
        }

        @Override
        public int getBackground(int position) {
            return 0;
        }

//        @Override
//        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//            return view == object;
//        }

//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            TextView tv = new TextView(getContext());
//            tv.setTextColor(Color.RED);
//            tv.setGravity(Gravity.CENTER);
//            tv.setText(titleString.get(position));
//            tv.setTextSize(18);
//            tv.setPadding(0,0,0,0);
//            container.addView(tv);
//            return tv;
//        }
//
//        @Override
//        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            container.removeView((View) object);
//        }
    }

    private class NavigationAdapter extends BaseQuickAdapter<NavigationDataList,BaseViewHolder>{

        public NavigationAdapter(int layoutResId, @Nullable List<NavigationDataList> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, NavigationDataList item) {
            if (!TextUtils.isEmpty(item.getName())) {
                helper.setText(R.id.item_navigation_recyclerView_title, item.getName());
            }

            TagFlowLayout flowLayout = helper.getView(R.id.id_flowlayout);
            List<NavigationJsonData.DataBean.ArticlesBean> mArticles = item.getArticlesBeans();
            flowLayout.setAdapter(new TagAdapter<NavigationJsonData.DataBean.ArticlesBean>(mArticles) {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public View getView(FlowLayout parent, int position, NavigationJsonData.DataBean.ArticlesBean articlesBean) {
                    TagFlowLayout mTagFlowLayout = helper.getView(R.id.id_flowlayout);

                    TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_layout_tv,
                            flowLayout,false);
                    tv.setPadding(10,10,10,10);
                    tv.setText(articlesBean.getTitle());
                    tv.setTextSize(14);
                    tv.setTextColor(CommonUtils.randomColor());
                    tv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP){
                                Intent intent = new Intent(getContext(), TbsWebView.class);
                                intent.putExtra("url",articlesBean.getLink());
                                intent.putExtra("title",articlesBean.getTitle());
                                intent.putExtra("author",articlesBean.getAuthor());
                                startActivity(intent);
                            }
                            return true;
                        }
                    });
//                    mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
//                        @Override
//                        public boolean onTagClick(View view, int position, FlowLayout parent) {
//                            Intent intent = new Intent(getContext(), TbsWebView.class);
//                            intent.putExtra("url",articlesBean.getLink());
//                            intent.putExtra("title",articlesBean.getTitle());
//                            intent.putExtra("author",articlesBean.getAuthor());
//                            startActivity(intent);
//                            return true;
//                        }
//                    });
                    return tv;

                }
            });
        }
    }

}
