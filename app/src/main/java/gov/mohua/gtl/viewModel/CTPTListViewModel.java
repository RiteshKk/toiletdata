package gov.mohua.gtl.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import gov.mohua.gtl.ToiletLocatorApp;
import gov.mohua.gtl.model.C;
import gov.mohua.gtl.model.CTPTDataModel;
import gov.mohua.gtl.model.CTPTResponseModel;
import gov.mohua.gtl.utility.AbsentLiveData;
import gov.mohua.gtl.utility.LiveDataWrapper;

public class CTPTListViewModel extends AndroidViewModel {

    public LiveData<LiveDataWrapper<CTPTResponseModel>> mDataModel;
    public MutableLiveData<Boolean> isLoadData;
    public ObservableInt showLoading;

    public CTPTListViewModel(@NonNull Application application) {
        super(application);
        isLoadData = new MutableLiveData<>();
        isLoadData.setValue(true);
        showLoading = new ObservableInt();
        mDataModel = Transformations.switchMap(isLoadData, flag -> {
            if (flag) {
                return callDataAPI();
            }
            return AbsentLiveData.create();
        });
    }

    private LiveData<LiveDataWrapper<CTPTResponseModel>> callDataAPI() {
        showLoading.set(View.VISIBLE);
        Context context = getApplication().getApplicationContext();
        String mobile = context.getSharedPreferences("toiletLocator", Context.MODE_PRIVATE).getString(C.Companion.getMOBILE(), "");
        MutableLiveData<LiveDataWrapper<CTPTResponseModel>> mObservableDataModel = new MutableLiveData<>();
        RequestQueue requestQueue = ToiletLocatorApp.Companion.getInstance().getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://sbmtoilet.org/backend/web/index.php?r=api/user/view&role=ctpt&mobile_no=" + mobile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.length() > 0) {
                    showLoading.set(View.GONE);
                    //Convert to ArrayList
                    CTPTResponseModel ctptDataModel = null;
                    try {
                        ctptDataModel = new Gson().fromJson(response.toString(), CTPTResponseModel.class);
                        for (int i = 0; i < ctptDataModel.getData().size(); i++) {
                            CTPTDataModel model = ctptDataModel.getData().get(i);

                            if (context.getSharedPreferences(C.Companion.getPREF_NAME(), Context.MODE_PRIVATE).getString(C.Companion.getROLE(), "").equalsIgnoreCase(C.Companion.getTHIRD_PARTY())) {
                                String thirdPartyCtPtRating = model.getThird_party_ct_pt_rating();
                                if (!TextUtils.isEmpty(thirdPartyCtPtRating)) {
                                    String[] split = thirdPartyCtPtRating.split("-");
                                    if (split.length > 1) {
                                        model.setThird_party_ct_pt_rating(split[1]);
                                    }
                                    model.setAnswer(split[0]);
                                }
                            } else {

                                String ctPtRating = model.getCt_pt_rating();
                                if (!TextUtils.isEmpty(ctPtRating)) {
                                    String[] split = ctPtRating.split("-");
                                    if (split.length > 1) {
                                        model.setCt_pt_rating(split[1]);
                                    }
                                    model.setAnswer(split[0]);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mObservableDataModel.setValue(LiveDataWrapper.success(ctptDataModel));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading.set(View.GONE);
                mObservableDataModel.setValue(LiveDataWrapper.failure(error));
            }
        });
        requestQueue.add(jsonObjectRequest);
        return mObservableDataModel;
    }

    @NotNull
    public LiveData<LiveDataWrapper<CTPTResponseModel>> getObserveData() {
        return mDataModel;
    }

    public void setLoadData(boolean value) {
        isLoadData.setValue(value);
    }

}
