package gov.mohua.gtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable {

    public String getMobile_no() {
        return mobile_no;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getRole() {
        return role;
    }

    private String mobile_no;
   private String city_id;
   private String role;

    private UserDetails(Parcel in) {
        mobile_no = in.readString();
        city_id = in.readString();
        role = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mobile_no);
        parcel.writeString(city_id);
        parcel.writeString(role);
    }
}
