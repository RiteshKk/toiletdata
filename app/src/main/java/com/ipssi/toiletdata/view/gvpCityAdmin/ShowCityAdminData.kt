package com.ipssi.toiletdata.view.gvpCityAdmin

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.ToiletLocatorApp
import com.ipssi.toiletdata.controller.CityAdminShowDataAdmin
import com.ipssi.toiletdata.events.OnCaptureButtonClickedListener
import com.ipssi.toiletdata.events.OnRadioButtonClickListener
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.CityAdminDataModel
import com.ipssi.toiletdata.model.ViewData
import kotlinx.android.synthetic.main.city_admin_show_data.*
import kotlinx.android.synthetic.main.city_admin_view_data.*
import org.json.JSONObject

class ShowCityAdminData : AppCompatActivity(), OnCaptureButtonClickedListener {
    override fun onUploadButtonClicked(data: ViewData) {
        setDeletedItem(data)
    }

    override fun onCaptureButtomClicked(viewData: ViewData?, position: Int) {
    }


    private lateinit var cityAdminShowDataAdmin: CityAdminShowDataAdmin

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_admin_view_data)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait...")
        progressDialog?.setCancelable(false)
        view_data.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cityAdminShowDataAdmin = CityAdminShowDataAdmin(this)
        val wardId = intent.getIntExtra(C.SELECTED_WARD_ID, 0);
        view_data.adapter = cityAdminShowDataAdmin
        fetchData(wardId)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getCategoryData(): JSONObject {
        val preferences = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        val categoryList = preferences.getString(C.CATEGORY_LIST, "")
        val categoryJson = JSONObject(categoryList)
        return categoryJson
    }

    private fun getWardData(): JSONObject {
        val preferences = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        val wardList = preferences.getString(C.WARD_LIST, "")
        val wardJson = JSONObject(wardList)
        return wardJson
    }


    fun fetchData(wardId:Int) {
        progressDialog.show()
        val preferences = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        val mobile = preferences.getString(C.MOBILE, "")
        val role = preferences.getString(C.ROLE, "")
        val stateId = preferences.getInt("stateId", -1)
        val cityId = preferences.getInt("cityId", -1)

        val stateName = preferences.getString(C.stateName, "")
        val cityName = preferences.getString(C.cityName, "")
        val url = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-view-data"
        val postParam = JSONObject()

        postParam.put("mobile_no", mobile).put("role", role).put("state_id", stateId).put("city_id", cityId).put("ward", wardId.toString());
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, postParam, Response.Listener {
            progressDialog.dismiss()
            val data = Gson().fromJson(it.toString(), CityAdminDataModel::class.java)
            cityAdminShowDataAdmin.setCategoryJSON(getCategoryData())
            cityAdminShowDataAdmin.setWardJSON(getWardData())

            cityAdminShowDataAdmin.setCity(cityName)
            cityAdminShowDataAdmin.setState(stateName)
            cityAdminShowDataAdmin.setData(data.data)
        }, Response.ErrorListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Ooops!! Something went wrong", Toast.LENGTH_SHORT).show()
        })

        requestQueue?.add(jsonRequest)
    }


    fun setDeletedItem(data: ViewData) {

        var progressDialog = ProgressDialog(this);
        progressDialog.setMessage("Please wait...")

        var preferences = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE);
        var mobile = preferences.getString(C.MOBILE, "");
        var role = preferences.getString(C.ROLE, "");
        var stateId = data.state_id;
        var cityId = data.city_id;
        val ward = data.ward_id
        val gvpId = data.gvp_id
        var url = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-deletion-request";
        var postParam = JSONObject();
        progressDialog.show();
        postParam.put("mobile_no", mobile).put("role", role).put("state_id", stateId).put("city_id", cityId).put("ward", ward).put("gvp_id", gvpId);
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, postParam, Response.Listener
        {
            progressDialog.dismiss()
            Toast.makeText(this, "successfully marked as deleted", Toast.LENGTH_SHORT).show()
        }, Response.ErrorListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Ooops!! Something went wrong", Toast.LENGTH_SHORT).show()
        })
        requestQueue?.add(jsonRequest)
    }
}
