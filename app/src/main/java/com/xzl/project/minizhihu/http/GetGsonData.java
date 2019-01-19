package com.xzl.project.minizhihu.http;

import com.google.gson.Gson;
import com.xzl.project.minizhihu.DataBean.ArticleJsonData;
import com.xzl.project.minizhihu.DataBean.BannerJsonData;
import com.xzl.project.minizhihu.DataBean.KnowledgeDeatilBean;
import com.xzl.project.minizhihu.DataBean.NavigationJsonData;
import com.xzl.project.minizhihu.DataBean.ProjectDataBean;
import com.xzl.project.minizhihu.DataBean.ProjectDeatilBean;
import com.xzl.project.minizhihu.DataBean.SearchResultDataBean;
import com.xzl.project.minizhihu.DataBean.TopSearchData;
import com.xzl.project.minizhihu.DataBean.TreeJsonData;
import com.xzl.project.minizhihu.DataBean.WallpaperDataBean;

public class GetGsonData {
    Gson gson;

    public GetGsonData(){
        gson = new Gson();
    }

    public BannerJsonData GetAllData(String json){
        BannerJsonData jsonData = gson.fromJson(json,BannerJsonData.class);
        return jsonData;
    }

    public ArticleJsonData GetNewsData(String json){
        ArticleJsonData jsonData = gson.fromJson(json,ArticleJsonData.class);
        return jsonData;
    }

    public TreeJsonData getTreeData(String json){
        TreeJsonData jsonData = gson.fromJson(json,TreeJsonData.class);
        return jsonData;
    }

    public KnowledgeDeatilBean getDeatilData(String json){
        KnowledgeDeatilBean bean = gson.fromJson(json,KnowledgeDeatilBean.class);
        return bean;
    }

    public NavigationJsonData GetNavigationData(String json){
        NavigationJsonData jsonData = gson.fromJson(json,NavigationJsonData.class);
        return jsonData;
    }

    public ProjectDataBean getProjectTreeData(String json){
        ProjectDataBean dataBean = gson.fromJson(json,ProjectDataBean.class);
        return dataBean;
    }

    public ProjectDeatilBean getProjectDeatilBean(String json){
        ProjectDeatilBean databean = gson.fromJson(json,ProjectDeatilBean.class);
        return databean;
    }

    public WallpaperDataBean getImageData(String json){
        WallpaperDataBean wallpaperDataBean = gson.fromJson(json,WallpaperDataBean.class);
        return wallpaperDataBean;
    }

    public TopSearchData getTopSearchData(String json){
        TopSearchData searchData = gson.fromJson(json,TopSearchData.class);
        return searchData;
    }

    public SearchResultDataBean getSeatchResultData(String json){
        SearchResultDataBean dataBean = gson.fromJson(json,SearchResultDataBean.class);
        return dataBean;
    }
}
