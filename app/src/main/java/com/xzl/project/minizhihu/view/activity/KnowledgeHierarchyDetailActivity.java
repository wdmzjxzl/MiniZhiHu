package com.xzl.project.minizhihu.view.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.xzl.project.minizhihu.DataBean.StructureData;
import com.xzl.project.minizhihu.DataBean.TreeJsonData;
import com.xzl.project.minizhihu.R;
import com.xzl.project.minizhihu.view.fragment.KnowledgeDeatilFragment;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeHierarchyDetailActivity extends AppCompatActivity implements Toolbar.OnClickListener{
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private List<KnowledgeDeatilFragment> mFragments;
    private String[] mTitles;
    private MyPagerAdapter adapter;
    private static final String TAG = "=========";
//    Toolbar toolbar;
//    TextView bartitle;
    private List<StructureData> dataList;
    private int positionId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_hierarchy_detail);
        initView();

        dataList = (List<StructureData>) getIntent().getSerializableExtra("dataList");
        positionId = getIntent().getIntExtra("position",0);

//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        assert actionBar != null;
//        actionBar.setDisplayShowTitleEnabled(false);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
//        bartitle.setText(dataList.get(positionId).getName());
//        toolbar.setNavigationOnClickListener(this);


        int i=0;
        mTitles = new String[dataList.get(positionId).getChildren().size()];
        for (TreeJsonData.DataBean.ChildrenBean dataBean : dataList.get(positionId).getChildren()){
            mTitles[i] = dataBean.getName();
            i++;
        }
        for (int i1=0;i1<mTitles.length;i1++){
//            mFragments.add(KnowledgeDeatilFragment.getInstant(dataList.get(positionId).getChildren().get(i1).getId()));
            KnowledgeDeatilFragment mf = new KnowledgeDeatilFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("positionID",dataList.get(positionId).getChildren().get(i1).getId());
            mf.setArguments(bundle);

            mFragments.add(mf);
        }
        viewPager.setOffscreenPageLimit(mTitles.length);

        adapter = new MyPagerAdapter(getSupportFragmentManager(),mFragments);
        viewPager.setAdapter(adapter);
        slidingTabLayout.setViewPager(viewPager,mTitles);
    }

    private void initView() {
        slidingTabLayout = findViewById(R.id.knowledge_hierarchy_detail_tab_layout);
        viewPager = findViewById(R.id.knowledge_hierarchy_detail_viewpager);
        mFragments = new ArrayList<>();
//        toolbar = findViewById(R.id.classification_toolbar);
//        bartitle = findViewById(R.id.classification_toolbar_title_tv);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        List<KnowledgeDeatilFragment> list;

        public MyPagerAdapter(FragmentManager fm,List<KnowledgeDeatilFragment> list){
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
