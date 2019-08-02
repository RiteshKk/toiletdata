package gov.mohua.gtl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StateDetail implements Parcelable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int id;
    private String name;
    private int code;

    private StateDetail(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readInt();
    }

    public static final Creator<StateDetail> CREATOR = new Creator<StateDetail>() {
        @Override
        public StateDetail createFromParcel(Parcel in) {
            return new StateDetail(in);
        }

        @Override
        public StateDetail[] newArray(int size) {
            return new StateDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(code);
    }
}
