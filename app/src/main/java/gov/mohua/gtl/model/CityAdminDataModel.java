package gov.mohua.gtl.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CityAdminDataModel implements Parcelable {
    private int status;

    public UserDetails getUser_details() {
        return user_details;
    }

    private StateDetail city_details;
    private StateDetail state_details;

    public ArrayList<ViewData> getData() {
        return data;
    }

    @SerializedName("user_detials")
    private UserDetails user_details;

    @SerializedName("view_data")
    private ArrayList<ViewData> data;


    protected CityAdminDataModel(Parcel in) {
        status = in.readInt();
        user_details = in.readParcelable(UserDetails.class.getClassLoader());
        data = in.createTypedArrayList(ViewData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeParcelable(user_details, flags);
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CityAdminDataModel> CREATOR = new Creator<CityAdminDataModel>() {
        @Override
        public CityAdminDataModel createFromParcel(Parcel in) {
            return new CityAdminDataModel(in);
        }

        @Override
        public CityAdminDataModel[] newArray(int size) {
            return new CityAdminDataModel[size];
        }
    };

    public StateDetail getCity_details() {
        return city_details;
    }

    public StateDetail getState_details() {
        return state_details;
    }
}
