package com.ipssi.toiletdata.viewModel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ipssi.toiletdata.BR;
import com.ipssi.toiletdata.ToiletLocatorApp;
import com.ipssi.toiletdata.model.C;
import com.ipssi.toiletdata.model.CTPTDataModel;
import com.ipssi.toiletdata.view.ctpt.CTPTFormDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CTPTListItemViewModel extends BaseObservable {
    private final Context mContext;
    private CTPTDataModel mDataModel;

    private int selectedItemPosition;

    public CTPTListItemViewModel(Context ctx, CTPTDataModel model) {
        mContext = ctx;
        selectedItemPosition = TextUtils.isEmpty(model.getAnswer()) ? 0 : Integer.parseInt(model.getAnswer());
        mDataModel = model;
    }

    @Bindable
    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
        notifyPropertyChanged(BR.selectedItemPosition);
    }

    public void onProceed(View v) {
//        if (selectedItemPosition != (TextUtils.isEmpty(mDataModel.getAnswer()) ? 0 : Integer.parseInt(mDataModel.getAnswer()))) {
        if (selectedItemPosition == 4) {
            sendNegativeResponse();
        } else {
            mDataModel.setAnswer(String.valueOf(selectedItemPosition));
            Intent intent = new Intent(mContext, CTPTFormDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("toilet_id", mDataModel);
            intent.putExtra("selected_position", selectedItemPosition);
            mContext.startActivity(intent);
        }
//        }
    }

    public void setObject(CTPTDataModel object, int position) {
        selectedItemPosition = TextUtils.isEmpty(object.getAnswer()) ? 0 : Integer.parseInt(object.getAnswer());
        this.mDataModel = object;
        notifyChange();
    }

    public String getAnswer() {
        return this.mDataModel.getAnswer();
    }

    public void setAnswer(String txt) {
        mDataModel.setAnswer(txt);
    }

    public String getCtptRating() {
        return mDataModel.getCt_pt_rating()==null?mDataModel.getThird_party_ct_pt_rating():mDataModel.getCt_pt_rating();
    }

    public CTPTDataModel getObject() {
        return mDataModel;
    }

    public String getId() {
        return mDataModel.getToilet_id();
    }

    public String getAddress() {
        return mDataModel.getAddress();
    }

    public String getGoogleMapLink() {
        return "https://www.google.com/maps/search/?api=1&query=" + mDataModel.getLatitude() + "," + mDataModel.getLongitude() + "&query_place_id=" + mDataModel.getPlace_id();
    }

    public String getQuestion() {
        return mDataModel.getPrimary_phn_no();
    }

    public void sendNegativeResponse() {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        JSONObject params = new JSONObject();
        SharedPreferences preferences = mContext.getSharedPreferences(C.Companion.getPREF_NAME(), Context.MODE_PRIVATE);
        try {

            params.put("user_id", preferences.getString(C.Companion.getMOBILE(), ""));
            params.put("toilet_id", mDataModel.getToilet_id());
            params.put("accessibility", 4);
            params.put("role", preferences.getString(C.Companion.getROLE(), ""));
            params.put("data", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = ToiletLocatorApp.Companion.getInstance().getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://sbmtoilet.org/backend/web/index.php?r=api/user/save-ct-pt-data", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                String status = response.optString("status");
                if (status.contains("No toilet existed")) {
                    setAnswer("4");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                setAnswer("0");
                Toast.makeText(mContext, "Oops something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(request);
    }

    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getGoogleMapLink()));
        mContext.startActivity(browserIntent);
    }
}
