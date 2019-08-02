package gov.mohua.gtl.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;

/**
 * Created by ipssi-mac on 12/9/16.
 */

public class LocationAPI {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final String TAG = "LocationApi";
    private static final int REQUEST_CHECK_SETTINGS = 101;

    public final ProgressDialog dialog;
    private final SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private WeakReference<Activity> contextHolder;
    private OnLocationChangeCallBack listener;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS ;
    private LocationCallback mLocationCallBack;


    public LocationAPI(Activity context, OnLocationChangeCallBack listener) {
        contextHolder = new WeakReference<Activity>(context);
        this.listener = listener;
        dialog = new ProgressDialog(contextHolder.get());
        dialog.setMessage("Getting Location Updates\nPlease Wait...");
        dialog.setCancelable(false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
        createLocationCallback();
        createLocationRequest();

    }


    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(contextHolder.get(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public void onStart() {
        buildLocationSettingsRequest();
        dialog.show();
        startLocationUpdates();
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                contextHolder.get().findViewById(android.R.id.content),
                contextHolder.get().getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(contextHolder.get().getString(actionStringId), listener).show();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(dialog!=null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                for (Location loc : locationResult.getLocations()) {
                    if(loc!=null && loc.getLatitude() > 0) {
                        Log.e("onLocationResult", "loc= " + loc.toString());
                        listener.onLocationChange(loc);
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
    }


    public void onStop() {
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder settingRequest = new LocationSettingsRequest.Builder();
        settingRequest.addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(contextHolder.get()).checkLocationSettings(settingRequest.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // start Location updates here
            }
        });

        task.addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                if(dialog!=null && dialog.isShowing()) {
                    Log.d("onCancel", "cancel Location updates");
                    dialog.dismiss();
                }
            }
        });

        task.addOnFailureListener(contextHolder.get(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if(dialog!=null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Log.e(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult(contextHolder.get(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        if(dialog!=null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(contextHolder.get(), errorMessage, Toast.LENGTH_LONG).show();
//                                mRequestingLocationUpdates = false;
                }
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        //  startLocationUpdate will call automatically from onResume
                        break;
                    case Activity.RESULT_CANCELED:
                        if(dialog!=null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Log.d("result cancel","cancel Location updates");
                        // The user was asked to change settings, but chose not to
                        // cancel Location updates
//                        mRequestingLocationUpdates = false;
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, Looper.myLooper());
    }
}


