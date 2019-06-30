package com.ipssi.toiletdata.view.ctpt

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ipssi.toiletdata.OnFragmentChangeListener
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.ToiletLocatorApp
import com.ipssi.toiletdata.databinding.ActivityCtptformDetailsBinding
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.CTPTDataModel
import com.ipssi.toiletdata.model.QuestionData
import com.ipssi.toiletdata.util.Utils
import com.ipssi.toiletdata.view.thirdParty.fragments.AdditionalFragment
import com.ipssi.toiletdata.view.thirdParty.fragments.DesirableFragment
import com.ipssi.toiletdata.view.thirdParty.fragments.EssentialFragment
import com.ipssi.toiletdata.view.thirdParty.fragments.MandatoryFragment
import org.json.JSONArray
import org.json.JSONObject


class CTPTFormDetails : AppCompatActivity(), OnFragmentChangeListener {
    val globalList = ArrayList<QuestionData>()
    override fun onFragmentChange(id: Int, bundle: Bundle?) {
        when (id) {
            0 -> {
                val mandatory = MandatoryFragment()
                val list = populateMandatoryDataList()
                globalList.addAll(list)
                mandatory.setList(list)
                mandatory.arguments = bundle
                supportFragmentManager.beginTransaction().addToBackStack(null).replace(binding.fragmentContainer.id, mandatory).commit()
            }
            1 -> {
                val essentialFragment = EssentialFragment()
                val list = populateEssentialDataList()
                essentialFragment.arguments = bundle
                globalList.addAll(list)
                essentialFragment.setList(list)
                supportFragmentManager.beginTransaction().addToBackStack(null).replace(binding.fragmentContainer.id, essentialFragment).commit()
            }
            2 -> {
                val desirableFragment = DesirableFragment()
                val list = populateDesiableDataList()
                globalList.addAll(list)
                desirableFragment.setList(list)
                desirableFragment.arguments = bundle
                supportFragmentManager.beginTransaction().addToBackStack(null).replace(binding.fragmentContainer.id, desirableFragment).commit()
            }
            3 -> {
                val additionalFragment = AdditionalFragment()
                val list = populateAdditionalDataList()
                additionalFragment.setList(list)
                globalList.addAll(list)
                additionalFragment.arguments = bundle
                supportFragmentManager.beginTransaction().addToBackStack(null).replace(binding.fragmentContainer.id, additionalFragment).commit()
            }
            4 -> {
                val mandatory = bundle?.getInt("mandatoryScore", 0) ?: 0
                val essential = bundle?.getInt("essentialScore", 0) ?: 0
                val desirable = bundle?.getInt("desirableScore", 0) ?: 0
                val additional = bundle?.getInt("additionalScore", 0) ?: 0
                var rating: String = ""
                if (mandatory >= 95 && essential >= 90 && desirable >= 80 && additional >= 90) {
                    rating = "5"
                } else if (mandatory >= 90 && essential >= 80 && desirable >= 70) {
                    rating = "4.5"
                } else if (mandatory >= 80 && essential >= 70 && desirable >= 60) {
                    rating = "4"
                } else if (mandatory >= 70 && essential >= 60) {
                    rating = "3"
                } else if (mandatory >= 60) {
                    rating = "2"
                } else if (mandatory > 0 && mandatory < 60) {
                    rating = "1"
                } else {
                    rating = "0"
                }
                uploadResponse(rating)
            }
            else -> supportFragmentManager.popBackStack()
        }
    }

    private lateinit var binding: ActivityCtptformDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val locator = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        val role = locator?.getString(C.ROLE, "")
        if (role.equals(C.THIRD_PARTY, true)) {
            supportActionBar?.setTitle("CTPT-Third-Party Validation Tool")
        }
        binding = DataBindingUtil.setContentView<ActivityCtptformDetailsBinding>(this, R.layout.activity_ctptform_details)
        binding.appVersion.text = Utils.getVersionName(this)
        onFragmentChange(0, null)
    }

    fun populateAdditionalDataList(): ArrayList<QuestionData> {
        var dataList = ArrayList<QuestionData>()
        val groupListType = object : TypeToken<ArrayList<QuestionData>>() {}.type
        dataList = Gson().fromJson(C.additionalData, groupListType)
        return dataList
    }

    fun populateEssentialDataList(): ArrayList<QuestionData> {
        var dataList = ArrayList<QuestionData>()
        val groupListType = object : TypeToken<ArrayList<QuestionData>>() {}.type
        dataList = Gson().fromJson(C.essentialData, groupListType)
        return dataList
    }

    fun populateDesiableDataList(): ArrayList<QuestionData> {
        var dataList = ArrayList<QuestionData>()
        val groupListType = object : TypeToken<ArrayList<QuestionData>>() {}.type
        dataList = Gson().fromJson(C.desirableData, groupListType)
        return dataList
    }

    fun populateMandatoryDataList(): ArrayList<QuestionData> {
        var dataList = ArrayList<QuestionData>()
        val groupListType = object : TypeToken<ArrayList<QuestionData>>() {}.type
        dataList = Gson().fromJson(C.mandatoryData, groupListType)
        return dataList
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (item?.itemId == R.id.menu_submit) {
//            uploadResponse()
//        } else
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadResponse(rating: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()
        val model = intent.getParcelableExtra<CTPTDataModel>("toilet_id")
        val params = JSONObject()
        val preferences = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        params.put("user_id", preferences?.getString(C.MOBILE, ""))
        params.put("toilet_id", model.toilet_id)
        params.put("accessibility", intent.getIntExtra("selected_position", 0))
        params.put("role", preferences?.getString(C.ROLE, ""))
        params.put("rating", rating)
//        params.put("state_id", preferences?.getInt("stateId", 0))
//        params.put("state_code", preferences?.getInt("stateCode", 0))
//        params.put("ulb_id", preferences?.getInt("cityId", 0))
//        params.put("city_code", preferences?.getInt("cityCode", 0))
        val convertResponseToJSON = convertResponseToJSON()
        params.put("data", convertResponseToJSON)
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()
        val request = JsonObjectRequest("http://sbmtoilet.org/backend/web/index.php?r=api/user/save-ct-pt-data", params, Response.Listener<JSONObject> { it ->
            progressDialog.dismiss()
            val status = it.optString("status")
            val totalMarks = it.optString("scored_marks")
            if (status.contains("Saved")) {
                Toast.makeText(applicationContext, "Data Saved Successfully!!", Toast.LENGTH_SHORT).show()
//                val returnIntent = Intent()
//                returnIntent.putExtra("id", model.toilet_id)
//                returnIntent.putExtra("marks", totalMarks)
//                setResult(Activity.RESULT_OK, returnIntent)
//                val intent = Intent(this, CTPTResultActivity::class.java)
//                intent.putParcelableArrayListExtra("data", list)
//                startActivity(intent)
                finish()
            }
        }, Response.ErrorListener { error ->
            progressDialog.dismiss()
            Snackbar.make(binding.root, "Oops something went wrong!", Toast.LENGTH_LONG).show()
        })

        requestQueue?.add(request)
    }

    private fun convertResponseToJSON(): JSONArray {
        var array = JSONArray()
        for (question in globalList) {
            if (!TextUtils.isEmpty(question.question)) {
                var ob = JSONObject()
                ob.put("ques_id", question.id)
                ob.put("selected_option_no", question.selectedOptionNo)
                array.put(ob)
            }
        }
        return array;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 105) {
            setResult(Activity.RESULT_OK)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            onFragmentChange(-1, null)
        } else {
            finish()
        }
    }
}
