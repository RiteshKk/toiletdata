package gov.mohua.gtl.view.gtl

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import gov.mohua.gtl.R
import gov.mohua.gtl.ToiletLocatorApp
import gov.mohua.gtl.controller.ToiletDataAdapter
import gov.mohua.gtl.model.C
import gov.mohua.gtl.model.ToiletData
import gov.mohua.gtl.util.RecyclerItemClickListener
import gov.mohua.gtl.util.Utils
import kotlinx.android.synthetic.main.content_list.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ListActivity : AppCompatActivity(), RecyclerItemClickListener.OnItemClickListener {

    private var progressDialog: ProgressDialog? = null
    private var adapter: ToiletDataAdapter? = null
    private var add: Request<JSONObject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_list)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Accessing data from Server\nPlease wait...")
        progressDialog?.setCancelable(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        (findViewById<TextView>(R.id.app_version)).text = Utils.getVersionName(this)


        val recyclerView = findViewById<RecyclerView>(R.id.list_users)
        val manager = LinearLayoutManager(this)
        recyclerView.layoutManager = manager
        adapter = ToiletDataAdapter()
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this, this))
        executeApi()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun executeApi() {
        //    9711681951
        progressDialog?.show()
        val url = "http://sbmtoilet.org/backend/web/index.php?r=api/user/view&mobile_no="
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()

        val mobile = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE).getString(C.MOBILE, "")
        val request = JsonObjectRequest(Request.Method.GET, url + mobile, null, Response.Listener { response ->
            progressDialog?.dismiss()
            try {
                val jsonArray = response.getJSONArray(C.DATA_TAG)
                Log.e("jsonArray", jsonArray.toString())
                val gson = GsonBuilder().serializeNulls().create()
                val typeData = object : TypeToken<ArrayList<ToiletData>>() {

                }.type
                val toiletDataList = gson.fromJson<List<ToiletData>>(jsonArray.toString(), typeData)
                adapter?.setToiletDataList(toiletDataList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            progressDialog?.dismiss()
            if (error is NoConnectionError || error is TimeoutError) {
                Snackbar.make(parent_container, "Can't Connect right now!", Snackbar.LENGTH_LONG).show()
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
                Snackbar.make(parent_container, "Server Error!", Snackbar.LENGTH_LONG).show()
                Log.e("volleyError",
                        "parsing error " + error.message)
            }
        })

        Log.e("request", request.url + "")
        progressDialog?.show()
        request.setShouldRetryServerErrors(true)
        request.retryPolicy = DefaultRetryPolicy(3000, 2, 0f)
        request.setShouldCache(false)
        add = requestQueue?.add(request)
    }

    override fun onItemClick(view: View, position: Int) {
        val toiletData = adapter?.getItem(position)
        val intent = Intent(this, gov.mohua.gtl.view.gtl.DetailsActivity::class.java)
        intent.putExtra(C.DATA_TAG, toiletData)
        startActivity(intent)
    }


    override fun onStop() {
        super.onStop()
        add?.cancel()
    }
}
