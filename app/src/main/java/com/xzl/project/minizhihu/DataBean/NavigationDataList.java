package com.xzl.project.minizhihu.DataBean;

import java.io.Serializable;
import java.util.List;

public class NavigationDataList implements Serializable {
    private String name;
    private String title;
    private String link;
    private List<NavigationJsonData.DataBean.ArticlesBean> articlesBeans;

    public List<NavigationJsonData.DataBean.ArticlesBean> getArticlesBeans() {
        return articlesBeans;
    }

    public void setArticlesBeans(List<NavigationJsonData.DataBean.ArticlesBean> articlesBeans) {
        this.articlesBeans = articlesBeans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
