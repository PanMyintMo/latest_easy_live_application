package com.gawilive.mall.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.File;

public class AddGoodsSpecBean extends GoodsSpecBean{

    @JSONField(serialize = false)
    private File mFile;
    @JSONField(serialize = false)
    private String mUploadResultUrl;

    private String spec_green_score;

    public String getSpec_green_score() {
        return spec_green_score;
    }

    public void setSpec_green_score(String spec_green_score) {
        this.spec_green_score = spec_green_score;
    }

    public File getmFile() {
        return mFile;
    }

    public void setmFile(File mFile) {
        this.mFile = mFile;
    }

    @JSONField(serialize = false)
    public File getFile() {
        return mFile;
    }

    @JSONField(serialize = false)
    public void setFile(File file) {
        mFile = file;
    }

    @JSONField(serialize = false)
    public boolean isEmpty() {
        return mFile == null && TextUtils.isEmpty(mThumb);
    }

    @JSONField(serialize = false)
    public void setEmpty() {
        mFile = null;
        mThumb = null;
    }


    @JSONField(serialize = false)
    public void setUploadResultUrl(String uploadResultUrl) {
        mUploadResultUrl = uploadResultUrl;
    }

    @JSONField(serialize = false)
    public String getUploadResultUrl() {
        if (!TextUtils.isEmpty(mUploadResultUrl)) {
            return mUploadResultUrl;
        }
        if (!TextUtils.isEmpty(mThumb)) {
            int index = mThumb.lastIndexOf("/") + 1;
            if (index > 0) {
                return mThumb.substring(index);
            }
        }
        return "";
    }

}
