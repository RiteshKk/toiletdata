package com.ipssi.toiletdata.view.nodalOfficer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.ipssi.toiletdata.model.ViewData;

import java.util.ArrayList;

public class NodalDataList implements Parcelable {
    protected NodalDataList(Parcel in) {
        status = in.readInt();
        user_details = in.readParcelable(UserDetails.class.getClassLoader());
        stateDetails = in.readParcelable(StateDetails.class.getClassLoader());
        cityDetails = in.readParcelable(StateDetails.class.getClassLoader());
        adminClickedImageList = in.createTypedArrayList(ImagePath.CREATOR);
        data = in.createTypedArrayList(ViewData.CREATOR);
        requestedDetails = in.createTypedArrayList(NodalGVPDetails.CREATOR);
    }

    public static final Creator<NodalDataList> CREATOR = new Creator<NodalDataList>() {
        @Override
        public NodalDataList createFromParcel(Parcel in) {
            return new NodalDataList(in);
        }

        @Override
        public NodalDataList[] newArray(int size) {
            return new NodalDataList[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserDetails getUser_details() {
        return user_details;
    }

    public void setUser_detials(UserDetails user_details) {
        this.user_details = user_details;
    }

    public StateDetails getStateDetails() {
        return stateDetails;
    }

    public void setStateDetails(StateDetails stateDetails) {
        this.stateDetails = stateDetails;
    }

    public StateDetails getCityDetails() {
        return cityDetails;
    }

    public void setCityDetails(StateDetails cityDetails) {
        this.cityDetails = cityDetails;
    }

    public ArrayList<ImagePath> getAdminClickedImageList() {
        return adminClickedImageList;
    }

    public void setAdminClickedImageList(ArrayList<ImagePath> adminClickedImageList) {
        this.adminClickedImageList = adminClickedImageList;
    }

    public ArrayList<ViewData> getData() {
        return data;
    }

    public void setData(ArrayList<ViewData> data) {
        this.data = data;
    }

    private int status;
    @SerializedName("user_details")
    private UserDetails user_details;
    @SerializedName("state_details")
    private StateDetails stateDetails;
    @SerializedName("city_details")
    private StateDetails cityDetails;
    @SerializedName("admin_clicked_image")
    private ArrayList<ImagePath> adminClickedImageList;

    @SerializedName("view_data")
    private ArrayList<ViewData> data;

    @SerializedName("requested_gvp")
    private ArrayList<NodalGVPDetails> requestedDetails;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeParcelable(user_details, flags);
        dest.writeParcelable(stateDetails, flags);
        dest.writeParcelable(cityDetails, flags);
        dest.writeTypedList(adminClickedImageList);
        dest.writeTypedList(data);
        dest.writeTypedList(requestedDetails);
    }

    public ArrayList<NodalGVPDetails> getRequestedDetails() {
        return requestedDetails;
    }

    public void setRequestedDetails(ArrayList<NodalGVPDetails> requestedDetails) {
        this.requestedDetails = requestedDetails;
    }
}
