package gov.mohua.gtl.view.gvpCityAdmin

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import gov.mohua.gtl.R
import gov.mohua.gtl.ToiletLocatorApp
import gov.mohua.gtl.model.C
import kotlinx.android.synthetic.main.city_admin_view_data.*
import org.json.JSONObject

class ShowCityAdminData : AppCompatActivity(),gov.mohua.gtl.events.OnCaptureButtonClickedListener {
    override fun onUploadButtonClicked(data:gov.mohua.gtl.model.ViewData) {
        setDeletedItem(data)
    }

    override fun onCaptureButtomClicked(viewData: gov.mohua.gtl.model.ViewData?,position: Int) {
    }


    private lateinit var cityAdminShowDataAdmin:gov.mohua.gtl.controller.CityAdminShowDataAdmin

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_admin_view_data)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        view_data.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cityAdminShowDataAdmin =gov.mohua.gtl.controller.CityAdminShowDataAdmin(this)
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


    private fun fetchData(wardId:Int) {
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
            val data = Gson().fromJson(it.toString(), gov.mohua.gtl.model.CityAdminDataModel::class.java)
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


    fun setDeletedItem(data:gov.mohua.gtl.model.ViewData) {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")

        val preferences = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE);
        val mobile = preferences.getString(C.MOBILE, "");
        val role = preferences.getString(C.ROLE, "");
        val stateId = data.state_id;
        val cityId = data.city_id;
        val ward = data.ward_id
        val gvpId = data.gvp_id
        val url = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-deletion-request";
        val postParam = JSONObject();
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
