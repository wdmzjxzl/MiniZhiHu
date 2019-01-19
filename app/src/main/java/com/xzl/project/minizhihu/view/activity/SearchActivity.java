package com.xzl.project.minizhihu.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xzl.project.minizhihu.Constants;
import com.xzl.project.minizhihu.DataBean.FeedArticleData;
import com.xzl.project.minizhihu.DataBean.TopSearchData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;
import com.xzl.project.minizhihu.utils.CommonUtils;
import com.xzl.project.minizhihu.utils.SearchListDBOperation;
import com.xzl.project.minizhihu.utils.T;
import com.xzl.project.minizhihu.view.SearchHistoryViewHolder;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static android.support.constraint.Constraints.TAG;

public class SearchActivity extends AppCompatActivity{

    @BindView(R.id.search_back_ib)
    ImageButton mBackIb;
    @BindView(R.id.search_edit)
    EditText mSearchEdit;
    @BindView(R.id.search_tv)
    TextView mSearchTv;
    @BindView(R.id.search_history_clear_all_tv)
    TextView mClearAllHistoryTv;
    @BindView(R.id.search_scroll_view)
    NestedScrollView mSearchScrollView;
    @BindView(R.id.search_history_null_tint_tv)
    TextView mHistoryNullTintTv;
    @BindView(R.id.search_history_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.top_search_flow_layout)
    TagFlowLayout mTopSearchFlowLayout;
    @BindView(R.id.search_qrchode)
    ImageView searchQRCode;

    private List<FeedArticleData> mTopSearchDataList;
//    private CircularRevealAnim mCircularRevealAnim;
    private HistorySearchAdapter historySearchAdapter;
    private List<String> searchRecordsList;
    private List<String> tempList;
    private SearchListDBOperation searchListDBOperation;

    private int REQUEST_CODE_SCAN = 11;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();
        getTopSearchData();
    }

    private void init() {
        mTopSearchDataList = new ArrayList<>();
        searchRecordsList = new ArrayList<>();
        tempList = new ArrayList<>();
        //传入表明，以对表进行操作
        searchListDBOperation = new SearchListDBOperation(SearchActivity.this,"building");
        tempList.addAll(searchListDBOperation.getRecordsList());
        reversedList();//倒序输出
        historySearchAdapter = new HistorySearchAdapter(R.layout.item_search_history,searchRecordsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mRecyclerView.setAdapter(historySearchAdapter);

        if (searchRecordsList.size()>0){
            mHistoryNullTintTv.setVisibility(View.GONE);

        }else {
            mHistoryNullTintTv.setVisibility(View.VISIBLE);
        }

        searchQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });
    }

    /**
     * 添加搜索历史
     * @param view
     */
    public void searchButton(View view){
        if (mSearchEdit.getText().toString().length()>0){
            String record = mSearchEdit.getText().toString();
            //判断数据库中是否存在该记录
            if (!searchListDBOperation.isHasRecord(record)){
                tempList.add(record);
            }
            //将记录保存至数据库中
            searchListDBOperation.addRecords(record);
            reversedList();
            historySearchAdapter.notifyDataSetChanged();
            mSearchEdit.setText("");
            mHistoryNullTintTv.setVisibility(View.GONE);
            Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
            intent.putExtra("searchText",record);
            startActivity(intent);
            this.finish();
        }else {
            T.s(SearchActivity.this,"搜索内容不能为空");
        }
    }

    private void getTopSearchData() {
        String url = "http://www.wanandroid.com//hotkey/json";
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
                setTopSearchData(response.body().string());
            }
        });
    }

    private void setTopSearchData(String string) {
        FeedArticleData feedArticleData;
        TopSearchData topSearchData = new GetGsonData().getTopSearchData(string);
        List<TopSearchData.DataBean> dataBeans = topSearchData.getData();
        for (int i=0;i<dataBeans.size();i++){
            TopSearchData.DataBean dataBean = dataBeans.get(i);
            feedArticleData = new FeedArticleData();
            feedArticleData.setSearchTopData(dataBean.getName());
            feedArticleData.setSearchTopDataId(dataBean.getId());
            mTopSearchDataList.add(feedArticleData);
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
                    mTopSearchFlowLayout.setAdapter(new TagAdapter(mTopSearchDataList) {

                        @Override
                        public View getView(FlowLayout parent, int position, Object o) {
                            TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.flow_layout_tv,
                                    parent, false);
                            tv.setPadding(10,10,10,10);
                            tv.setText(mTopSearchDataList.get(position).getSearchTopData());
                            tv.setTextSize(15);
                            setItemBackground(tv);
                            tv.setTextColor(CommonUtils.randomColor());

                            mTopSearchFlowLayout.setOnTagClickListener((view, position1, parent1) -> {
                                if (!searchListDBOperation.isHasRecord(mTopSearchDataList.get(position1).getSearchTopData())){
                                    tempList.add(mTopSearchDataList.get(position1).getSearchTopData());
                                }
                                searchListDBOperation.addRecords(mTopSearchDataList.get(position1).getSearchTopData());
                                reversedList();
                                historySearchAdapter.notifyDataSetChanged();
                                mHistoryNullTintTv.setVisibility(View.GONE);
                                Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
                                intent.putExtra("searchText",mTopSearchDataList.get(position1).getSearchTopData());
                                startActivity(intent);
                                finish();
                                return true;
                            });
                            return tv;
                        }
                    });
                    break;
            }
        }
    };

    /**
     * 设置热搜词背景
     * @param tv
     */
    private void setItemBackground(TextView tv) {
        tv.setBackgroundColor(CommonUtils.randomTagColor());
        tv.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.white));
    }

    /**
     * 清空历史记录
     * @param view
     */
    public void ClearAll(View view) {
        tempList.clear();
        reversedList();
        searchListDBOperation.deleteAllRecords();
        historySearchAdapter.notifyDataSetChanged();
        mHistoryNullTintTv.setVisibility(View.VISIBLE);
    }

    /**
     * 颠倒list顺序，用户输入的信息会从上往下依次显示
     */
    private void reversedList() {
        searchRecordsList.clear();
        for (int i = tempList.size()-1; i>=0;i--){
            searchRecordsList.add(tempList.get(i));
        }
    }

    public void thisFinish(View view) {
        this.finish();
    }

    public class HistorySearchAdapter extends BaseQuickAdapter<String, SearchHistoryViewHolder> {

        public HistorySearchAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(SearchHistoryViewHolder helper, String item) {
            helper.setText(R.id.item_search_history_tv, item);
            helper.setTextColor(R.id.item_search_history_tv, CommonUtils.randomColor());
//        helper.addOnClickListener(R.id.item_search_history_tv);
            helper.setOnClickListener(R.id.item_search_history_tv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
                    intent.putExtra("searchText",item);
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //扫描二维码、条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK){
            if (data!=null){
                Intent intent = new Intent(SearchActivity.this,TbsWebView.class);
                intent.putExtra("url",data.getStringExtra(Constant.CODED_CONTENT));
                intent.putExtra("title","二维码扫描结果");
                intent.putExtra("author","null");
                startActivity(intent);
            }
        }
    }
}
