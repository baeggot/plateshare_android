package com.baeflower.sol.plateshare.model;

import android.text.TextUtils;

/**
 * Created by sol on 2015-06-16.
 */
public class UserInfo {

    private long mId;

    private String mEmail;
    private String mUniv; // 대학교
    private String mPassword; // 비밀번호
    private String mTextLocation; // 텍스트 위치
    private Double mLatitude; // 지도
    private Double mLongitude; // 지도


    public UserInfo() {
    }

    public UserInfo(String mEmail, String mPassword, String mUniv, String mTextLocation, Double mLatitude, Double mLongitude) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mUniv = mUniv;
        this.mTextLocation = mTextLocation;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmUniv() {
        return mUniv;
    }

    public void setmUniv(String mUniv) {
        this.mUniv = mUniv;
    }


    public String getmTextLocation() {
        return mTextLocation;
    }

    public void setmTextLocation(String mTextLocation) {
        this.mTextLocation = mTextLocation;
    }


    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public boolean isAllSet() {
        if (TextUtils.isEmpty(mEmail) == false
                && TextUtils.isEmpty(mUniv) == false
                && TextUtils.isEmpty(mPassword) == false
                && TextUtils.isEmpty(mTextLocation) == false
                && mLatitude != 0
                && mLongitude != 0) {
            return true;
        }

        return false;
    }


}
