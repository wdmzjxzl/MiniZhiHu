package com.xzl.project.minizhihu.widget;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.WriterException;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.listener.PermissionsListener;
import com.xzl.project.minizhihu.utils.PermissionManager;
import com.xzl.project.minizhihu.utils.T;
import com.xzl.project.minizhihu.view.activity.MainActivity;
import com.yzq.zxinglibrary.encode.CodeCreator;
import java.util.List;

public class CustomDialog extends Dialog {
    private Context context;
    private ImageView QRCodeImageView;
    private ImageView QQShareImageView;
    private ImageView WeChatImageView;
    private String shareUrl;
    private TextView shareText;
    Bitmap bitmap = null;


    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId,String shareURL) {
        super(context, themeResId);
        this.context = context;
        this.shareUrl = shareURL;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.item_dialog,null);
        this.setContentView(layout);

        shareText = findViewById(R.id.share_text);
        QRCodeImageView = findViewById(R.id.qrcode_img);
        QQShareImageView = findViewById(R.id.qq_share);
        WeChatImageView = findViewById(R.id.wechat_share);

        Window window = this.getWindow();

        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        if (TextUtils.isEmpty(shareUrl)){
            shareText.setText("生成失败,请重试");
            return;
        }



        try {
            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            bitmap = CodeCreator.createQRCode(shareUrl,400,400,logo);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        QRCodeImageView.setImageBitmap(bitmap);

        QQShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PlatformUtil.isInstallAPP(context,PlatformUtil.PACKAGE_MOBILE_QQ)) {
                    try {
                        T.s(context,"正在分享到QQ...");
                        Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
                                context.getContentResolver(), bitmap, null, null));
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.setType("image/*");
                        // 遍历所有支持发送图片的应用。找到需要的应用
                        ComponentName componentName = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");

                        shareIntent.setComponent(componentName);
                        // mContext.startActivity(shareIntent);
                        context.startActivity(Intent.createChooser(shareIntent, "Share"));
                    } catch (Exception e) {
//            ContextUtil.getInstance().showToastMsg("分享图片到**失败");
                        T.s(context,"分享图片到QQ失败");
                    }
                }
            }
        });

        WeChatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlatformUtil.isInstallAPP(context,PlatformUtil.PACKAGE_MOBILE_QQ)) {
                    try {
                        T.s(context,"正在分享到微信...");
                        Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
                                context.getContentResolver(), bitmap, null, null));
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.setType("image/*");
                        // 遍历所有支持发送图片的应用。找到需要的应用
                        ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");

                        shareIntent.setComponent(componentName);
                        // mContext.startActivity(shareIntent);
                        context.startActivity(Intent.createChooser(shareIntent, "Share"));

                    } catch (Exception e) {
//            ContextUtil.getInstance().showToastMsg("分享图片到**失败");
                        T.s(context,"分享图片到微信失败");
                    }
                }
            }
        });
    }

    private static class PlatformUtil{
        public static final String PACKAGE_WECHAT = "com.tencent.mm";
        public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
        public static final String PACKAGE_QZONE = "com.qzone";
        public static final String PACKAGE_SINA = "com.sina.weibo";

        public static boolean isInstallAPP(Context context,String app_package){
            final PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> pINfo = packageManager.getInstalledPackages(0);
            if (pINfo != null){
                for (int i=0;i<pINfo.size();i++){
                    String pn = pINfo.get(i).packageName;
                    if (app_package.equals(pn)){
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
