package gov.mohua.gtl.view.nodalOfficer

import android.app.ProgressDialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import gov.mohua.gtl.R
import gov.mohua.gtl.ToiletLocatorApp
import gov.mohua.gtl.model.C
import kotlinx.android.synthetic.main.nodal_officer_view_details.*
import org.json.JSONObject

class NodalOfficerDetails : AppCompatActivity() {

    private var viewData: gov.mohua.gtl.model.ViewData? = null

    private lateinit var binding:gov.mohua.gtl.databinding.NodalOfficerViewDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.nodal_officer_view_details);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewData = intent.getParcelableExtra(C.DATA_TAG)
        fetchDetails()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }


    private var dataList: gov.mohua.gtl.view.nodalOfficer.model.NodalDataList? = null

    private fun fetchDetails() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        val url = "http://sbmtoilet.org/backend/web/index.php?r=api/user/update-specific-gvp-record"
        val params = JSONObject()
        progressDialog.show()
        val preference = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        params.put("mobile_no", preference?.getString(C.MOBILE, ""))
        params.put("role", preference?.getString(C.ROLE, ""))
        params.put("state_id", viewData?.state_id)
        params.put("city_id", viewData?.city_id)
        params.put("ward", viewData?.ward_id)
        params.put("gvp_id", viewData?.gvp_id)
        val volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, params, Response.Listener { response ->
            progressDialog.dismiss()
            Log.e("response", response.toString())
            if (response.toString().length > 5) {
                dataList = Gson().fromJson(response.toString(), gov.mohua.gtl.view.nodalOfficer.model.NodalDataList::class.java)
                if (dataList?.status == 200) {
                    binding.viewModel = dataList?.requestedDetails?.get(0)
                    if (dataList?.requestedDetails?.size!! > 1) {
                        image_layout_2.visibility = View.VISIBLE
                        commet2.setText(dataList?.requestedDetails?.get(1)?.comment)
                        Picasso.with(this@NodalOfficerDetails).load(C.IMAGE_BASE_URL +dataList?.requestedDetails?.get(1)?.image_path).fit().into(image2)
                    }
                    if (dataList?.requestedDetails?.size!! > 2) {
                        image_layout_3.visibility = View.VISIBLE
                        commet3.setText(dataList?.requestedDetails?.get(2)?.comment)
                        Picasso.with(this@NodalOfficerDetails).load(C.IMAGE_BASE_URL +dataList?.requestedDetails?.get(2)?.image_path).fit().into(image3)
                    }
                }
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

}
