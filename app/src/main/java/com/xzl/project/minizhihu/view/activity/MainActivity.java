package com.xzl.project.minizhihu.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.sdk.QbSdk;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.listener.PermissionsListener;
import com.xzl.project.minizhihu.utils.PermissionManager;
import com.xzl.project.minizhihu.view.fragment.ApplicationFragment;
import com.xzl.project.minizhihu.view.fragment.FavoritesListFragment;
import com.xzl.project.minizhihu.view.fragment.NavigationFragment;
import com.xzl.project.minizhihu.view.fragment.ProjectFragment;
import com.xzl.project.minizhihu.view.fragment.SettingFragment;
import com.xzl.project.minizhihu.view.fragment.StructureFragment;
import com.xzl.project.minizhihu.utils.T;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button[] mTabs;
    public static int CODE_WRITE_EXTERNAL_STORAGE=1;
    private int index,currentTabIndex;
    private Fragment[] fragments;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private DrawerLayout mDrawe;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private PermissionManager helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bugly.init(getApplicationContext(), "751bc56ae9", false);
        Bmob.initialize(this, "38d45cc88f26f6cf5bede8689e3aa361");
//        CrashReport.initCrashReport(getApplicationContext(), "751bc56ae9", true);
        //初始化界面
        initView();

        try {
            Log.e("====", "onCreate: "+this.getApplicationContext().getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitle.setText(R.string.app_name);

        //加载腾讯X5内核
        QbSdk.initX5Environment(MainActivity.this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("@@","加载内核是否成功:"+b);
            }
        });

        /** 实例化Fragment */
        ApplicationFragment applicationFragment = new ApplicationFragment();
        NavigationFragment navigationFragment = new NavigationFragment();
        ProjectFragment projectFragment = new ProjectFragment();
        StructureFragment structureFragment = new StructureFragment();
        FavoritesListFragment favoritesListFragment = new FavoritesListFragment();
        SettingFragment settingFragment = new SettingFragment();

        fragments = new Fragment[] {applicationFragment,structureFragment,navigationFragment,projectFragment,favoritesListFragment,settingFragment};

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,applicationFragment)
                .add(R.id.fragment_container,structureFragment)
                .add(R.id.fragment_container,navigationFragment)
                .add(R.id.fragment_container,projectFragment)
                .add(R.id.fragment_container,favoritesListFragment)
                .add(R.id.fragment_container,settingFragment)
                .hide(structureFragment)
                .hide(navigationFragment)
                .hide(projectFragment)
                .hide(favoritesListFragment)
                .hide(settingFragment)
                .show(applicationFragment)
                .commit();

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawe,toolbar,0,0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

        };

        mDrawe.setDrawerListener(mDrawerToggle);
        if (mNavigationView==null) Log.d("aaa", "onCreate: ");

        mNavigationView.setNavigationItemSelectedListener(this);

        test23();
    }


    public void test23() {
        Map<String,String> hashMap = new HashMap<>();
        hashMap.put("AccID", "123");
        hashMap.put("ConsumeDT", "20181023124046");
        hashMap.put("PerMoney", "6000");
        hashMap.put("UpMoney", "-2000");
        System.out.println("1111111111"+a(hashMap));
    }


    public String a(Map<String, String> map) {
        List<String> arrayList = new ArrayList(map.keySet());
        Collections.sort(arrayList);
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : arrayList) {
            stringBuffer.append(str);
            if (map.get(str) != null) {
                stringBuffer.append((String) map.get(str));
            }
        }
        stringBuffer.append("sign-kailu=");
        String str2 = null;
        try {
            str2 = a(stringBuffer.toString().trim());
        } catch (Exception e) {
        }
        return str2;
    }


    public static String a(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuffer stringBuffer = new StringBuffer("");
            for (int i : digest) {
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(Integer.toHexString(i));
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initView() {
        mNavigationView = findViewById(R.id.main_navifation);
        mTabs = new Button[4];
        mTabs[0] = findViewById(R.id.btn_homepage);
        mTabs[1] = findViewById(R.id.btn_structure);
        mTabs[2] = findViewById(R.id.btn_navigation);
        mTabs[3] = findViewById(R.id.btn_pro);
        //选择第一个选项卡
        mTabs[0].setSelected(true);
        toolbar = findViewById(R.id.common_toolbar);
        toolbarTitle = findViewById(R.id.common_toolbar_title_tv);
        mDrawe = findViewById(R.id.myDrawer);

        helper=PermissionManager.with(this)
                .addRequestCode(MainActivity.CODE_WRITE_EXTERNAL_STORAGE)

                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.READ_LOGS)
                .setPermissionsListener(new PermissionsListener() {
                    @Override
                    public void onGranted() {
                        //当权限被授予时调用
                    }

                    @Override
                    public void onDenied() {
                        //用户拒绝该权限时调用
                        T.s(MainActivity.this,"您已拒绝授权，APP若干功能需要权限方可运行，如果您需要使用APP某些功能，请根据提示进行授权");
                    }

                    @Override
                    public void onShowRationale(String[] permissions) {
                        //当用户拒绝某权限事并点击“不再提醒”的按钮是，需要给出合适的响应(比如扎实对话框来解释)
                        T.s(MainActivity.this,"APP若干功能需要权限方可运行，如果您需要使用APP某些功能，请根据提示进行授权");
                    }
                }).request();

    }

    /**
     * 菜单按钮点击
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_homepage:
                index = 0;
//                Toast.makeText(MainActivity.this,"点击了按钮",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_structure:
                index = 1;
                break;
            case R.id.btn_navigation:
                index = 2;
                break;
            case R.id.btn_pro:
                index = 3;
                break;
        }
        selecteBtn(index);
    }

    /**
     * 选定主Activity下的fragment
     * @param index
     */
    private void selecteBtn(int index){
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 设置当前按钮选择
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_usage:
                startActivity(new Intent(this,WallpaperActivity.class));
                break;
            case R.id.action_search:
                startActivity(new Intent(this,SearchActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
                T.s(MainActivity.this,"再按一次退出程序");
                firstTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_item_about_us:
                //关于我们
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
            case R.id.nav_item_logout:
                //用户反馈
                startActivity(new Intent(MainActivity.this,FeedbackActivity.class));
                break;
            case R.id.nav_item_my_collect:
                //收藏列表
                BmobUser bmobUser = BmobUser.getCurrentUser();
                if (bmobUser != null){
                    startActivity(new Intent(MainActivity.this,CollectionActivity.class));
                }else {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
                break;
        }
        mDrawe.closeDrawer(GravityCompat.START);
        return true;
    }
}
