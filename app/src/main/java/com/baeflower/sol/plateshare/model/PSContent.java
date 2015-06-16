package com.baeflower.sol.plateshare.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sol on 2015-06-09.
 */
public class PSContent implements Parcelable {

    private int mId;
    private String mTitle;
    private Bitmap mBitImage;

    public PSContent(){}

    public PSContent(Parcel in) {
        readFromParcel(in);
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Bitmap getmBitImage() {
        return mBitImage;
    }

    public void setmBitImage(Bitmap mBitImage) {
        this.mBitImage = mBitImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeParcelable(mBitImage, CONTENTS_FILE_DESCRIPTOR);
    }

    private void readFromParcel(Parcel in){
        mId = in.readInt();
        mTitle = in.readString();
        mBitImage = in.readParcelable(ClassLoader.getSystemClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PSContent createFromParcel(Parcel in) {
            return new PSContent(in);
        }

        @Override
        public PSContent[] newArray(int size) {
            return new PSContent[0];
        }
    };



}
