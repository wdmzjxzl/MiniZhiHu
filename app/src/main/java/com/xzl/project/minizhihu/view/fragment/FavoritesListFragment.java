package com.xzl.project.minizhihu.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xzl.project.minizhihu.BaseFragment;
import com.xzl.project.minizhihu.R;

public class FavoritesListFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setView(setLayout(inflater,container, R.layout.fragment_favorites_list),this);

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
