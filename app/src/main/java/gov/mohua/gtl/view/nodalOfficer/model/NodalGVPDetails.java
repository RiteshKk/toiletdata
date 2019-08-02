package gov.mohua.gtl.view.nodalOfficer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NodalGVPDetails implements Parcelable {
    protected NodalGVPDetails(Parcel in) {
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        gvp_cat = in.readString();
        admin_img = in.readString();
        admin_comment = in.readString();
        admin_date = in.readString();
        image_path = in.readString();
        comment = in.readString();
        created_at = in.readString();
    }

    public static final Creator<NodalGVPDetails> CREATOR = new Creator<NodalGVPDetails>() {
        @Override
        public NodalGVPDetails createFromParcel(Parcel in) {
            return new NodalGVPDetails(in);
        }

        @Override
        public NodalGVPDetails[] newArray(int size) {
            return new NodalGVPDetails[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getGvp_cat() {
        return gvp_cat;
    }

    public void setGvp_cat(String gvp_cat) {
        this.gvp_cat = gvp_cat;
    }

    public String getAdmin_img() {
        return admin_img;
    }

    public void setAdmin_img(String admin_img) {
        this.admin_img = admin_img;
    }

    public String getAdmin_comment() {
        return admin_comment;
    }

    public void setAdmin_comment(String admin_comment) {
        this.admin_comment = admin_comment;
    }

    public String getAdmin_date() {
        return admin_date;
    }

    public void setAdmin_date(String admin_date) {
        this.admin_date = admin_date;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    private String address;
   private String latitude;
   private String longitude;
   private String gvp_cat;
   private String admin_img;
   private String admin_comment;
   private String admin_date;
   private String image_path;
   private String comment;
   private String created_at;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(gvp_cat);
        dest.writeString(admin_img);
        dest.writeString(admin_comment);
        dest.writeString(admin_date);
        dest.writeString(image_path);
        dest.writeString(comment);
        dest.writeString(created_at);
    }
}
