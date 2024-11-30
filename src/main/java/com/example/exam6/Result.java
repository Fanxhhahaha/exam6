package com.example.exam6;

import java.util.List;

public class Result {
    private int curpage;
    private int allnum;
    private List<NewsItem> newslist;

    // getters and setters
    public int getCurpage() { return curpage; }
    public void setCurpage(int curpage) { this.curpage = curpage; }
    public int getAllnum() { return allnum; }
    public void setAllnum(int allnum) { this.allnum = allnum; }
    public List<NewsItem> getNewslist() { return newslist; }
    public void setNewslist(List<NewsItem> newslist) { this.newslist = newslist; }
}