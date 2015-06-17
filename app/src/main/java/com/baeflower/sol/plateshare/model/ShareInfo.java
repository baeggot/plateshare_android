package com.baeflower.sol.plateshare.model;

import java.util.Date;

/**
 * Created by sol on 2015-06-16.
 */
public class ShareInfo {

    private long mId;
    private String mTitle;
    private String mExplanation;
    private String mImageName;
    private String mLocation;
    private String mMapLocation;

    private Date mDday;

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmExplanation() {
        return mExplanation;
    }

    public void setmExplanation(String mExplanation) {
        this.mExplanation = mExplanation;
    }

    public String getmImageName() {
        return mImageName;
    }

    public void setmImageName(String mImageName) {
        this.mImageName = mImageName;
    }

    public Date getmDday() {
        return mDday;
    }

    public void setmDday(Date mDday) {
        this.mDday = mDday;
    }


    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmMapLocation() {
        return mMapLocation;
    }

    public void setmMapLocation(String mMapLocation) {
        this.mMapLocation = mMapLocation;
    }
}
