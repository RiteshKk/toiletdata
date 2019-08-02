package gov.mohua.gtl.view.nodalOfficer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StateDetails implements Parcelable {
    protected StateDetails(Parcel in) {
        name = in.readString();
        code = in.readInt();
    }

    public static final Creator<StateDetails> CREATOR = new Creator<StateDetails>() {
        @Override
        public StateDetails createFromParcel(Parcel in) {
            return new StateDetails(in);
        }

        @Override
        public StateDetails[] newArray(int size) {
            return new StateDetails[size];
        }
    };

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

    private String name;
    private int code;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(code);
    }
}
