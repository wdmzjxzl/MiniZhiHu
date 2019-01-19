package com.xzl.project.minizhihu.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.xzl.project.minizhihu.R;

public class PhotoViewAvtivity extends Activity {
//    PhotoView photoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_view);
//        photoView = findViewById(R.id.photo_view);

        Intent intent = getIntent();
//        Glide.with(PhotoViewAvtivity.this).load(intent.getStringExtra("curImageUrl")).into(photoView);
//
////        photoView.setImageURI(Uri.parse(intent.getStringExtra("curImageUrl")));
//        photoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }
}
