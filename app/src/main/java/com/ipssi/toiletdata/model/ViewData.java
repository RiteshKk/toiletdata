package com.ipssi.toiletdata.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ViewData implements Parcelable {
    public int getId() {
        return id;
    }

    public String getGvp_id() {
        return gvp_id;
    }

    public int getState_id() {
        return state_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public int getWard_id() {
        return ward_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public int getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getCreated_by() {
        return created_by;
    }

    private int id;
    private String gvp_id;
    private int state_id;
    private int city_id;
    private int ward_id;
    private String image_path;
    private int category;
    private String address;
    private double latitude;
    private double longitude;
    private String comment;
    private String created_at;
    private int created_by;
    public Bitmap nodalOfficerImage;
    public String imageString;
    public String nodalOfficerComment;
    private int cleaned;
    private int deletion_request;
    private String nodal_gvp_id;
    private String nodal_img;
    private String nodal_comment;
    private String nodal_clicked_img;

    public String getNodal_gvp_id() {
        return nodal_gvp_id;
    }

    public void setNodal_gvp_id(String nodal_gvp_id) {
        this.nodal_gvp_id = nodal_gvp_id;
    }

    public String getNodal_img() {
        return nodal_img;
    }

    public void setNodal_img(String nodal_img) {
        this.nodal_img = nodal_img;
    }

    public String getNodal_comment() {
        return nodal_comment;
    }

    public void setNodal_comment(String nodal_comment) {
        this.nodal_comment = nodal_comment;
    }

    public String getNodal_clicked_img() {
        return nodal_clicked_img;
    }

    public void setNodal_clicked_img(String nodal_clicked_img) {
        this.nodal_clicked_img = nodal_clicked_img;
    }


    public int getCleaned() {
        return cleaned;
    }

    public void setCleaned(int cleaned) {
        this.cleaned = cleaned;
    }

    public int getDeletion_request() {
        return deletion_request;
    }

    public void setDeletion_request(int deletion_request) {
        this.deletion_request = deletion_request;
    }


    private ViewData(Parcel in) {
        id = in.readInt();
        gvp_id = in.readString();
        state_id = in.readInt();
        city_id = in.readInt();
        ward_id = in.readInt();
        image_path = in.readString();
        category = in.readInt();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        comment = in.readString();
        created_at = in.readString();
        created_by = in.readInt();
        cleaned = in.readInt();
        deletion_request = in.readInt();
        nodal_gvp_id = in.readString();
        nodal_img = in.readString();
        nodal_comment = in.readString();
        nodal_clicked_img = in.readString();

    }

    public static final Creator<ViewData> CREATOR = new Creator<ViewData>() {
        @Override
        public ViewData createFromParcel(Parcel in) {
            return new ViewData(in);
        }

        @Override
        public ViewData[] newArray(int size) {
            return new ViewData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(gvp_id);
        parcel.writeInt(state_id);
        parcel.writeInt(city_id);
        parcel.writeInt(ward_id);
        parcel.writeString(image_path);
        parcel.writeInt(category);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(comment);
        parcel.writeString(created_at);
        parcel.writeInt(created_by);
        parcel.writeInt(cleaned);
        parcel.writeInt(deletion_request);
        parcel.writeString(nodal_gvp_id);
        parcel.writeString(nodal_img);
        parcel.writeString(nodal_comment);
        parcel.writeString(nodal_clicked_img);
    }
}
