package com.xzl.project.minizhihu.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.xzl.project.minizhihu.BaseFragment;
import com.xzl.project.minizhihu.DataBean.ProjectDataBean;
import com.xzl.project.minizhihu.DataBean.ProjectListData;
import com.xzl.project.minizhihu.DataBean.ProjectTabData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;

public class ProjectFragment extends BaseFragment {
    private SlidingTabLayout tabLayout;
//    private View project_divider;
    private ViewPager viewPager;
    private final String url = "http://www.wanandroid.com/project/tree/json";
    private List<ProjectTabData> tabDataList;
    private List<ProjectListData> listDataList;
    private String[] mTitles;
    private List<ProjectListFragment> mFragment;

    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setView(setLayout(inflater , container , R.layout.fragment_project) , this);
        initView(view);
        getProjectJsonData();

        return view;
    }



    private void initView(View view) {
        tabLayout = view.findViewById(R.id.project_tablayout);
//        project_divider = view.findViewById(R.id.project_divider);
        viewPager = view.findViewById(R.id.project_viewpager);
        tabDataList = new ArrayList<>();
        listDataList = new ArrayList<>();
        mFragment = new ArrayList<>();
    }

    private void getProjectJsonData() {
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
                setData(response.body().string());
            }
        });
    }

    private void setData(String jsonString){
        ProjectTabData tabData;

        ProjectDataBean projectDataBean = new GetGsonData().getProjectTreeData(jsonString);
        List<ProjectDataBean.DataBean> dataBeans = projectDataBean.getData();
        mTitles = new String[dataBeans.size()];
        for (int i=0;i<dataBeans.size();i++){
            ProjectDataBean.DataBean dataBean = dataBeans.get(i);
            tabData = new ProjectTabData();
            tabData.setName(dataBean.getName());
            tabData.setId(dataBean.getId());
            mTitles[i] = dataBean.getName();
            ProjectListFragment listFragment = new ProjectListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id",dataBean.getId());
            listFragment.setArguments(bundle);

            mFragment.add(listFragment);
            tabDataList.add(tabData);
        }

        Message message = new Message();
        message.what = 0x11;
        handler.sendMessage(message);

    }

    private class ProjectViewPagerAdapter extends FragmentPagerAdapter{
        List<ProjectListFragment> list;

        public ProjectViewPagerAdapter(FragmentManager fm,List<ProjectListFragment> list) {
            super(fm);
            this.list = list;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x11:
                    viewPager.setAdapter(new ProjectViewPagerAdapter(getFragmentManager(),mFragment));
                    viewPager.setOffscreenPageLimit(mTitles.length);
                    tabLayout.setViewPager(viewPager,mTitles);
                    break;
            }
        }
    };
}
