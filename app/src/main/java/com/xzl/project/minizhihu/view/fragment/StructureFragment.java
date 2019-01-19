package com.xzl.project.minizhihu.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xzl.project.minizhihu.BaseFragment;
import com.xzl.project.minizhihu.DataBean.StructureData;
import com.xzl.project.minizhihu.DataBean.TreeJsonData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.view.adapter.TreeAdapter;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.view.activity.KnowledgeHierarchyDetailActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StructureFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener{
    private String TAG = "=======";
    RecyclerView recyclerView;
    private SmartRefreshLayout mRefreshLayout;

    String url = "http://www.wanandroid.com/tree/json";
    OkHttpClient client;
    Request.Builder builder;
    Request request;
    Call call;
    List<StructureData> dataList;
    TreeAdapter treeAdapter;


    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setView(setLayout(inflater , container , R.layout.fragment_structure) , this);
        initView(view);

        getBaseData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setViewData(R.layout.item_structure,recyclerView);

        //设置下拉刷新
        setRefresh(mRefreshLayout);

        treeAdapter.setOnItemClickListener(this);
        return view;
    }

    private void initView(View v) {
        recyclerView = v.findViewById(R.id.knowledge_System);
        mRefreshLayout = v.findViewById(R.id.structure_refresh);

        //OKHTTPclient对象
        client = new OkHttpClient();
        //构造Request
        //builder.get()代表是get请求，URL方法里面放的参数是一个网络地址
        builder = new Request.Builder();
        request = builder.get().url(url).build();
        //将request封装成call
        call = client.newCall(request);
        dataList = new ArrayList<>();
    }


    public void getBaseData(){

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
                setAnalysisData(response.body().string());
            }
        });
    }

    /** 解析数据 */
    private void setAnalysisData(String string){
//        List<String> childrenName = new ArrayList<>();
        StructureData structureData;
        TreeJsonData treeJsonData = new GetGsonData().getTreeData(string);
        List<TreeJsonData.DataBean> dataBeans = treeJsonData.getData();

        for (int i = 0;i <dataBeans.size(); i++){
            TreeJsonData.DataBean dataBean = dataBeans.get(i);
            structureData = new StructureData();
            structureData.setName(dataBean.getName());
            structureData.setChildren(dataBean.getChildren());
            for (TreeJsonData.DataBean.ChildrenBean data : dataBean.getChildren()){
                structureData.setId(data.getId());
                structureData.setCourseName(data.getName());
//                Log.e(TAG, "setAnalysisData: "+structureData.getCourseName());
            }
//            List<TreeJsonData.DataBean.ChildrenBean> childrenBeans = dataBean.getChildren();
//
//            for (int y = 0;y < childrenBeans.size(); y++){
//                TreeJsonData.DataBean.ChildrenBean childrenBean = childrenBeans.get(y);
//                childrenName.add(childrenBean.getName());
//
//            }
            dataList.add(structureData);
        }
    }

    /** 设置适配器 填充数据 */
    public void setViewData(int viewLayout, RecyclerView recyclerView){
        treeAdapter = new TreeAdapter(viewLayout,dataList);
        treeAdapter.openLoadAnimation(BaseQuickAdapter.FOOTER_VIEW);
        recyclerView.setAdapter(treeAdapter);
    }

    /**
     * 设置知识体系列表下拉刷新  这里是个假的下拉刷新
     * @param refreshLayout
     */
    public void setRefresh(SmartRefreshLayout refreshLayout){
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                treeAdapter.notifyDataSetChanged();
                refreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getContext(),KnowledgeHierarchyDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("dataList", (Serializable) dataList);
        bundle.putInt("position",position);
//        Log.e(TAG, "onItemClick: "+position );
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
