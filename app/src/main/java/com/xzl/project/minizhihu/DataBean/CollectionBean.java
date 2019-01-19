package com.xzl.project.minizhihu.DataBean;

import cn.bmob.v3.BmobObject;

public class CollectionBean extends BmobObject {
    private String title;
    private String author;
    private String link;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
