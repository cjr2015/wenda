package com.nowcoder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageInfo<T> {

    private int curPageNum;//当前页码
    private int prePageNum;//前一页页码
    private int nextPageNum;//后一页页码
    private int sumInfoCount;//记录总条数
    private int pageCount;//总页数
    private int perPageInfoCount = 10;//每页记录条数
    private List<T> curPageInfoList = new ArrayList<>();
    private Map<String,String> InfoMap = new HashMap<>();

    public int getSumInfoCount() {
        return sumInfoCount;
    }

    public void setSumInfoCount(int sumInfoCount) {
        this.sumInfoCount = sumInfoCount;
    }

    public int getCurPageNum() {

        return curPageNum;
    }

    public void setCurPageNum(int curPageNum) {
        this.curPageNum = curPageNum;
    }

    public int getPrePageNum() {
        return prePageNum;
    }

    public void setPrePageNum(int prePageNum) {
        this.prePageNum = prePageNum;
    }

    public int getNextPageNum() {
        return nextPageNum;
    }

    public void setNextPageNum(int nextPageNum) {
        this.nextPageNum = nextPageNum;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount() {
        this.pageCount = sumInfoCount % perPageInfoCount == 0? sumInfoCount / perPageInfoCount : (sumInfoCount / perPageInfoCount)+1;
    }

    public int getPerPageInfoCount() {
        return perPageInfoCount;
    }

    public List<T> getCurPageInfoList() {
        return curPageInfoList;
    }

    public void setCurPageInfoList(List<T> curPageInfoList) {
        this.curPageInfoList = curPageInfoList;
    }
}
