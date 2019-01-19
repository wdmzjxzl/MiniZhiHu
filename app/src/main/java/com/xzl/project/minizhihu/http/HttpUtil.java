package com.xzl.project.minizhihu.http;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    private OkHttpClient okHttpClient;
    private static HttpUtil httpUtil;
    private final Handler handler;

    private HttpUtil(){
        //创建一个主线程的handler
        handler = new Handler(Looper.getMainLooper());
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000,TimeUnit.MILLISECONDS)
                .readTimeout(5000,TimeUnit.MILLISECONDS)
                .build();
    }

    //设置外部访问的方法
    public static HttpUtil getInstance(){
        if (httpUtil == null){
            synchronized (HttpUtil.class){
                if (httpUtil == null){
                    return httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    /**
     * get请求
     */
    public void doGet(String url,final onCallBack onCallBack){
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //切换到主线程
                if (onCallBack!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onCallBack.onFaild(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response!=null&&response.isSuccessful()){
                                String json = response.body().string();
                                if (onCallBack!=null){
                                    onCallBack.onResponse(json);
                                    return;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (onCallBack!=null){
                            onCallBack.onFaild(
                                    new Exception("异常")
                            );
                        }
                    }
                });
            }
        });
    }

    /**
     * post请求
     * @param url
     * @param map
     * @param onCallBack
     */
    public void doPost(String url, Map<String,String> map,final onCallBack onCallBack){
        FormBody.Builder builder = new FormBody.Builder();
        for (String key:map.keySet()){
            builder.add(key,map.get(key));
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (onCallBack!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onCallBack.onFaild(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response!=null&&response.isSuccessful()){
                                String json = response.body().string();
                                if (onCallBack!=null){
                                    onCallBack.onResponse(json);
                                    return;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if (onCallBack!=null){
                            onCallBack.onFaild(new Exception("有异常"));
                        }
                    }
                });
            }
        });
    }

    public interface onCallBack{
        void onFaild(Exception e);
        void onResponse(String json);
    }
}
