package com.xzl.project.minizhihu.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dingmouren.layoutmanagergroup.echelon.EchelonLayoutManager;
import com.xzl.project.minizhihu.DataBean.FeedArticleData;
import com.xzl.project.minizhihu.DataBean.WallpaperDataBean;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.http.GetGsonData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static android.support.constraint.Constraints.TAG;

public class WallpaperActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbar_text;
    RecyclerView mRecyclerView;
    private List<FeedArticleData> feedArticleData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        setTitle("");
        initView();

        setSupportActionBar(toolbar);
        toolbar_text.setText("玩什么安卓？看小姐姐啊！");
        getImageData();
    }

    private void initView() {
        toolbar = findViewById(R.id.wallpaper_toolbar);
        toolbar_text = findViewById(R.id.wallpaper_title);
        mRecyclerView = findViewById(R.id.wallpaper_recyclerview);
    }

    private void getImageData(){
        int position = randomInt();
        Log.e(TAG, "getImageData: "+position );
        String url = "http://wallpaper.apc.360.cn/index.php?c=WallPaperAndroid&a=getAppsByCategory&cid=6&start="+position+"&count=99";


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
//                jsonString = response.body().string();
                setData(response.body().string());
            }
        });
    }

    private void setData(String json){
        FeedArticleData articleData;
        WallpaperDataBean wallpaperDataBean = new GetGsonData().getImageData(json);
        List<WallpaperDataBean.DataBean> dataBeans = wallpaperDataBean.getData();
        for (int i=0;i<dataBeans.size();i++){
            WallpaperDataBean.DataBean dataBean = dataBeans.get(i);
            articleData = new FeedArticleData();
            articleData.setWallpaperImag(dataBean.getUrl());
            feedArticleData.add(articleData);
        }
        Message message = new Message();
        message.what = 0x15;
        handler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x15:
                    MyAdapter myAdapter = new MyAdapter(R.layout.item_cardslide,feedArticleData);
                    mRecyclerView.setLayoutManager(new EchelonLayoutManager(WallpaperActivity.this));
                    mRecyclerView.setAdapter(myAdapter);
                    break;
            }
        }
    };


    /**
     * 获取3800内的一个随机数
     *
     * @return
     */
    private int randomInt(){
        Random random = new Random();
        return random.nextInt(3000);//最大3800
    }

//    class ViewHolder{
//        ImageView imageView;
//
//        public ViewHolder(View view){
//            imageView = view.findViewById(R.id.card_image_view);
//        }
//
//        public void bindData(FeedArticleData feedArticleData){
//            Glide.with(WallpaperActivity.this).load(feedArticleData.getWallpaperImag()).into(imageView);
//        }
//    }

    private class MyAdapter extends BaseQuickAdapter<FeedArticleData,BaseViewHolder>{

        public MyAdapter(int layoutResId, @Nullable List<FeedArticleData> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, FeedArticleData item) {
            ImageView imageView = helper.getView(R.id.card_image_view);
            Glide.with(WallpaperActivity.this).load(item.getWallpaperImag()).into(imageView);
        }
    }

}
