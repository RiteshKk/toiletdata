package gov.mohua.gtl.view.gvpCityAdmin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import gov.mohua.gtl.R
import gov.mohua.gtl.ToiletLocatorApp
import gov.mohua.gtl.model.C
import gov.mohua.gtl.util.Utils
import gov.mohua.gtl.view.nodalOfficer.NodalOfficerWardList
import kotlinx.android.synthetic.main.activity_city_admin.*
import org.json.JSONObject
import java.util.*

class CityAdminActivity : AppCompatActivity() {

    private lateinit var gvpId: String

    private lateinit var progressDialog: ProgressDialog

    private var selectedWardId: Int? = -1

    private var selectedWardText: String? = ""

    private lateinit var locator: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_admin)

        locator = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        if (locator.getString(C.ROLE, "").equals(C.NODAL_OFFICER, true)) {
            getSupportActionBar()?.title = "GVP Monitoring"
        } else {
            getSupportActionBar()?.title = "GVP Mapping"
        }
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)


        val cityCode = locator.getInt("cityCode", 0) ?: 0

        val stateCode = locator.getInt("stateCode", 0) ?: 0

        edt_state?.hint = (Html.fromHtml(getString(R.string.state_name)))
        edt_state_code?.hint = (Html.fromHtml(getString(R.string.state_code)))
        edt_city?.hint = (Html.fromHtml(getString(R.string.city)))
        edt_city_sensus_code?.hint = (Html.fromHtml(getString(R.string.city_sensus_code)))

        edt_state.editText?.setText(Html.fromHtml(locator.getString(C.stateName, "")))
        edt_state_code.editText?.setText(Html.fromHtml(stateCode.toString()))
        edt_city.editText?.setText(Html.fromHtml(locator.getString(C.cityName, "")))
        edt_city_sensus_code.editText?.setText(Html.fromHtml(cityCode.toString()))
        app_version.text = Utils.getVersionName(this)

        val categoryListJson = locator.getString(C.WARD_LIST, "")

        val json = JSONObject(categoryListJson)
        val keys = json.keys()
        val catList = ArrayList<Pair<Int, String>>()
        if (keys != null) {
            while (keys.hasNext()) {
                val key = keys.next()
                val value = json.optString(key)
                catList.add(android.util.Pair(key.toInt(), value))
            }
        }

        ward_list.adapter =gov.mohua.gtl.controller.CategoryListAdapter(this,android.R.layout.simple_spinner_dropdown_item,catList)
        ward_list.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p1 != null) {
                    val selectedItem = p0?.selectedItem
                    val pair = selectedItem as Pair<Int, String>
                    (p1 as TextView).text = pair.second
                    selectedWardText = pair.second
                    selectedWardId = pair.first
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        btn_proceed.isEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (locator.getString(C.ROLE, "").equals(C.NODAL_OFFICER, true))
            menu?.findItem(R.id.menu_show)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_logout) {
            locator.edit()?.putBoolean(C.IS_LOGGED_IN, false)?.apply()
            finish()
        } else if (item?.itemId == R.id.menu_show) {
            val intent = Intent(this@CityAdminActivity, ShowCityAdminData::class.java)
            intent.putExtra(C.SELECTED_WARD_ID,selectedWardId)
            startActivity(intent)
        }else if(item?.itemId == R.id.menu_user_manual){
            val role = locator.getString(C.ROLE, "")
            var url = ""
            if (role.equals(C.GVP_ADMIN, ignoreCase = true)) {
                url = C.GVP_MAPPING_MANUAL
            } else if (role.equals(C.NODAL_OFFICER, true)) {
                url = C.GVP_NODAL_MANUAL
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }else if(item?.itemId == R.id.menu_feedback){
            val url = "${C.FEEDBACK_LINK}user_id=${locator.getString(C.MOBILE, "0")
                    ?: "0"}&state_id=${locator.getInt("stateId", 0)
                    ?: 0}&city_id=${locator.getInt("cityId", 0) ?: 0}"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(url))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClick(v: View) {
        getGVPId()
    }


    fun getGVPId() {
        progressDialog.show()
        val params = JSONObject()
        params.put("mobile_no",locator.getString(C.MOBILE, ""))
        params.put("role",locator.getString(C.ROLE, ""))
        params.put("state_id",locator.getInt("stateId", -1))
        params.put("city_id",locator.getInt("cityId", -1))
        params.put("ward", selectedWardId)
        val URL = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-id-request"
        val volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URL, params, Response.Listener {response ->
            progressDialog.dismiss()
            Log.e("response", response.toString())
            val code = response.getString("code")
            if (code.equals("200")) {
                gvpId = response.getString("gvp_id")
                if (locator.getString(C.ROLE, "").equals(C.GVP_ADMIN, true)) {
                    val intent = Intent(this, CityAdminForm::class.java)
                    intent.putExtra(C.SELECTED_WARD, selectedWardText)
                    intent.putExtra(C.SELECTED_WARD_ID, selectedWardId)
                    intent.putExtra(C.GVP_ID, gvpId)
                    startActivityForResult(intent, 2001)
                } else {
                    val intent = Intent(this, NodalOfficerWardList::class.java)
                    intent.putExtra(C.SELECTED_WARD, selectedWardText)
                    intent.putExtra(C.SELECTED_WARD_ID, selectedWardId)
                    intent.putExtra(C.GVP_ID, gvpId)
                    startActivityForResult(intent, 2001)
                }
                btn_proceed.isEnabled = false
            }

        }, Response.ErrorListener { error ->
            progressDialog.dismiss()
            val view = parent_container
            if (view != null) {
                if (error is NoConnectionError || error is TimeoutError) {
                    Snackbar.make(view, "Can't Connect right now!", Snackbar.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Log.e("volleyError",
                            "auth Error " + error.message)
                } else if (error is ServerError) {
                    Log.e("volleyError",
                            "server error " + error.message)
                } else if (error is NetworkError) {
                    Log.e("volleyError",
                            "network error " + error.message)
                } else if (error is ParseError) {
                    Snackbar.make(view, "Server Error!", Snackbar.LENGTH_LONG).show()
                    Log.e("volleyError",
                            "parsing error " + error.message)
                }
            }
        })
        volleyRequest?.add(jsonObjectRequest)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2001 && resultCode == Activity.RESULT_OK) {
            ward_list.setSelection(0)
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }
}

