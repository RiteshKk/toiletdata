package com.ipssi.toiletdata.location;

import android.location.Location;

/**
 * Created by ipssi-mac on 12/9/16.
 */

public interface OnLocationChangeCallBack {
    int ACCESS_FINE_LOCATION_CONSTANT = 10;
    int REQUEST_PERMISSION_SETTING = 12;

    void onLocationChange(Location loc);
}
