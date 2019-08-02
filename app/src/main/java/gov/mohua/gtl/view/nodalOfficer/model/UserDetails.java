package gov.mohua.gtl.view.nodalOfficer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable {
    private String mobile_no;
    private int city_id;
    private String role;

    protected UserDetails(Parcel in) {
        mobile_no = in.readString();
        city_id = in.readInt();
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

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobile_no);
        dest.writeInt(city_id);
        dest.writeString(role);
    }
}
