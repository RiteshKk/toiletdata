package gov.mohua.gtl.view.validation

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import gov.mohua.gtl.R
import gov.mohua.gtl.ToiletLocatorApp
import gov.mohua.gtl.events.OnFragmentInteractionListener
import gov.mohua.gtl.model.C
import gov.mohua.gtl.model.ToiletData
import gov.mohua.gtl.util.Utils
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginFragment : Fragment(), View.OnClickListener {

    private var inputLayoutLogin: TextInputLayout? = null

    private var mListener: OnFragmentInteractionListener? = null
    private var toiletData: ToiletData? = null
    private var progressDialog: ProgressDialog? = null
    private var requestAdded: Request<JSONObject>? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is OnFragmentInteractionListener) {
            mListener = activity
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(context)
        progressDialog?.setMessage("Validating data from Server\nPlease wait...")
        progressDialog?.setCancelable(false)
        toiletData = arguments?.getParcelable("data")
        inputLayoutLogin = view.findViewById(R.id.login_layout)

        app_version.text = Utils.getVersionName(activity)


        sign_in_button.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        val activity = activity
        val inputMethodManager = activity!!
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val iBinder = view?.windowToken
        if (iBinder != null) {
            inputMethodManager.hideSoftInputFromWindow(iBinder, 0)
        }

        val loginId = inputLayoutLogin?.editText?.text.toString()
        if (TextUtils.isEmpty(loginId)) {
            inputLayoutLogin?.error = "Field can't be Empty"
            return
        } else {
            inputLayoutLogin?.error = ""
        }

        val url = "http://gtl.axters.com/backend/web/index.php?r=api/user/login&mobile_no="
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()
        val request = JsonObjectRequest(Request.Method.GET, url + loginId, null, Response.Listener { response ->
            progressDialog?.dismiss()
            try {
                val status = response.getString("status")
                progressDialog?.dismiss()
                Snackbar.make(view!!, status, Snackbar.LENGTH_LONG).show()
                val role = response.getString(C.ROLE)
                if (status.equals("Logged in successfully.")) {

                    val stateDetails = response.optJSONObject("state_details")
                    val stateId = stateDetails.optInt("id")
                    val stateName = stateDetails.optString("name")
                    val stateCode = stateDetails.optInt("code")
                    val cityDetails = response.optJSONObject("ulb_details")
                    val cityId = cityDetails.optInt("id")
                    val cityName = cityDetails.optString("name")
                    val cityCode = cityDetails.optInt("code")
                    val categoryList = response.optJSONObject("gvp_categories")
                    val wardList = response.optJSONObject("wards")

                    activity.getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)?.edit()
                            ?.putString(C.WARD_LIST,wardList?.toString())
                            ?.putString(C.CATEGORY_LIST,categoryList?.toString())
                            ?.putString(C.MOBILE, loginId)
                            ?.putInt("stateId", stateId)
                            ?.putString("stateName", stateName)
                            ?.putInt("stateCode", stateCode)
                            ?.putInt("cityId", cityId)
                            ?.putString("cityName", cityName)
                            ?.putInt("cityCode", cityCode)
                            ?.apply()
                    activity.getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedIn", true)
                            .putString(C.ROLE, role)
                            .apply()
                    if (role.equals(C.CTPT, true)) {
                        mListener?.onFragmentInteraction(C.CTPT)
                    } else if (role.equals(C.THIRD_PARTY, true)) {
                        mListener?.onFragmentInteraction(C.THIRD_PARTY)
                    } else if (role.equals(C.ULB, true)) {
                        mListener?.onFragmentInteraction("1")
                    } else if (role.equals(C.GVP_ADMIN, ignoreCase = true)) {
                        mListener?.onFragmentInteraction(C.GVP_ADMIN)
                    }else if(role.equals(C.NODAL_OFFICER,true)){
                        mListener?.onFragmentInteraction(C.NODAL_OFFICER)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            progressDialog?.dismiss()
            if (error is NoConnectionError || error is TimeoutError) {
                Snackbar.make(view!!, "Can't Connect right now!", Snackbar.LENGTH_LONG).show()
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
                Snackbar.make(view!!, "Server Error!", Snackbar.LENGTH_LONG).show()
                Log.e("volleyError",
                        "parsing error " + error.message)
            }
        })

        progressDialog?.show()
        request.tag = "login"
        requestAdded = requestQueue?.add(request)

    }

    override fun onStop() {
        super.onStop()
        requestAdded?.cancel()
    }
}
