package com.baeflower.sol.plateshare.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sol on 2015-06-16.
 */
public class ShareInfo implements Parcelable {

    private long mId;
    private String mTitle;
    private String mExplanation;
    private String mImageName;

    private String mTextLocation;
    private Date mDday;
    private Date mCreatedDate;

    private Double mLatitude;
    private Double mLongitude;


    // 기본 생성자
    public ShareInfo() {
    }

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


    public String getmTextLocation() {
        return mTextLocation;
    }

    public void setmTextLocation(String mTextLocation) {
        this.mTextLocation = mTextLocation;
    }

    public Date getmCreatedDate() {
        return mCreatedDate;
    }

    public void setmCreatedDate(Date mCreatedDate) {
        this.mCreatedDate = mCreatedDate;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    // ------------------------------- Data Parsing
    public ShareInfo(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<ShareInfo> CREATOR = new Creator<ShareInfo>() {
        @Override
        public ShareInfo createFromParcel(Parcel in) {
            return new ShareInfo(in);
        }

        @Override
        public ShareInfo[] newArray(int size) {
            return new ShareInfo[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mExplanation);
        dest.writeString(mImageName);
        dest.writeString(mTextLocation);

        mSf = new SimpleDateFormat("yyyy/MM/dd");

        dest.writeString(mSf.format(mDday));
    }

    private SimpleDateFormat mSf;

    private void readFromParcel(Parcel in) {

        mSf = new SimpleDateFormat("yyyy/MM/dd");

        setmId(in.readLong());
        setmTitle(in.readString());
        setmExplanation(in.readString());
        setmImageName(in.readString());
        setmTextLocation(in.readString());

        try {
            setmDday(mSf.parse(in.readString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
