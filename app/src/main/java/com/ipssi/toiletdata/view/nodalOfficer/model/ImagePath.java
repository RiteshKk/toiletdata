package com.ipssi.toiletdata.view.nodalOfficer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ImagePath implements Parcelable {
    @SerializedName("image_path")
    private String imagePath;

    protected ImagePath(Parcel in) {
        imagePath = in.readString();
    }

    public static final Creator<ImagePath> CREATOR = new Creator<ImagePath>() {
        @Override
        public ImagePath createFromParcel(Parcel in) {
            return new ImagePath(in);
        }

        @Override
        public ImagePath[] newArray(int size) {
            return new ImagePath[size];
        }
    };

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
    }
}
