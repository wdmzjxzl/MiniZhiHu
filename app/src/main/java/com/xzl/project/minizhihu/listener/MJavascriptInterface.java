package com.xzl.project.minizhihu.listener;

import android.content.Context;
import android.content.Intent;
import com.xzl.project.minizhihu.view.activity.PhotoViewAvtivity;

public class MJavascriptInterface {
    private Context context;

    public MJavascriptInterface(Context context){
        this.context = context;
    }

    @android.webkit.JavascriptInterface
    public void openImage(String img){
//        Log.e("====", "openImage: 点击了图片" );
        Intent intent = new Intent();
        intent.putExtra("curImageUrl",img);
        intent.setClass(context, PhotoViewAvtivity.class);
        context.startActivity(intent);
    }
}
