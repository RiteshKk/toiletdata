package gov.mohua.gtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CTPTResponseModel implements Parcelable {
    protected CTPTResponseModel(Parcel in) {
        role = in.readString();
        stateDetail = in.readParcelable(StateDetail.class.getClassLoader());
        ulbDetails = in.readParcelable(StateDetail.class.getClassLoader());
        data = in.createTypedArrayList(CTPTDataModel.CREATOR);
    }

    public static final Creator<CTPTResponseModel> CREATOR = new Creator<CTPTResponseModel>() {
        @Override
        public CTPTResponseModel createFromParcel(Parcel in) {
            return new CTPTResponseModel(in);
        }

        @Override
        public CTPTResponseModel[] newArray(int size) {
            return new CTPTResponseModel[size];
        }
    };

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public StateDetail getStateDetail() {
        return stateDetail;
    }

    public void setStateDetail(StateDetail stateDetail) {
        this.stateDetail = stateDetail;
    }

    public StateDetail getUlbDetails() {
        return ulbDetails;
    }

    public void setUlbDetails(StateDetail ulbDetails) {
        this.ulbDetails = ulbDetails;
    }

    public ArrayList<CTPTDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<CTPTDataModel> data) {
        this.data = data;
    }

    @SerializedName("role")
    private String role;
    @SerializedName("state_details")
    private StateDetail stateDetail;
    @SerializedName("ulb_details")
    private StateDetail ulbDetails;
    @SerializedName("data")
    private ArrayList<CTPTDataModel> data;
    @SerializedName("total_marks")
    private int totalMarks;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(role);
        dest.writeParcelable(stateDetail, flags);
        dest.writeParcelable(ulbDetails, flags);
        dest.writeTypedList(data);
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }
}
