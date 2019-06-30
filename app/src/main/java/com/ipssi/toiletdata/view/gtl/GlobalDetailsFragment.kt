package com.ipssi.toiletdata.view.gtl

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.ToiletData
import com.ipssi.toiletdata.util.Utils
import com.ipssi.toiletdata.events.OnFragmentInteractionListener
import com.ipssi.toiletdata.util.ValidationError
import kotlinx.android.synthetic.main.fragment_global_details.*

class GlobalDetailsFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

//    override fun onLocationChange(loc: Location?) {
//
//        locationAPI.onStop()
//    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        this.activity = childFragment?.activity
    }

    private var activity: Activity? = null;
    private var etdState: TextInputLayout? = null
    private var etdStateCode: TextInputLayout? = null
    private var etdCity: TextInputLayout? = null
    private var etdCityCode: TextInputLayout? = null
    private var etdAssessorName: TextInputLayout? = null
    private var etdAssessorPhone: TextInputLayout? = null
    private var etdZone: TextInputLayout? = null
    private var etdWard: TextInputLayout? = null

    private var mListener: OnFragmentInteractionListener? = null
    private var category: Spinner? = null
    private var type: Spinner? = null
    //    private var validator: ViewValidator? = null
    private var toiletData: ToiletData? = null
    private var failureCounter = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_global_details, container, false)
    }

//    private lateinit var locationAPI: LocationAPI

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        locationAPI = LocationAPI(activity, this)

        toiletData = arguments?.getParcelable(C.DATA_TAG)
        etdState = view.findViewById(R.id.edt_state)
        etdStateCode = view.findViewById(R.id.edt_state_code)
        etdCity = view.findViewById(R.id.edt_city)
        etdCityCode = view.findViewById(R.id.edt_city_sensus_code)
        etdAssessorName = view.findViewById(R.id.edt_assessor_name)
        etdAssessorPhone = view.findViewById(R.id.edt_assessor_phone)
        etdZone = view.findViewById(R.id.edt_zone)
        etdWard = view.findViewById(R.id.edt_ward)

        category = view.findViewById(R.id.category)
        category?.onItemSelectedListener = this
        type = view.findViewById(R.id.type)
        type?.onItemSelectedListener = this


        etdState?.hint = Html.fromHtml(getString(R.string.select_state))
        etdStateCode?.hint = Html.fromHtml(getString(R.string.state_code))
        etdCity?.hint = Html.fromHtml(getString(R.string.city))
        etdCityCode?.hint = Html.fromHtml(getString(R.string.city_sensus_code))
        etdAssessorName?.hint = Html.fromHtml(getString(R.string.assessor_name))
        etdAssessorPhone?.hint = Html.fromHtml(getString(R.string.assessor_phone))
        etdZone?.hint = Html.fromHtml(getString(R.string.zone))
        etdWard?.hint = Html.fromHtml(getString(R.string.ward))



        app_version.text = Utils.getVersionName(activity)

        (view.findViewById<View>(R.id.btn_next) as ImageView).setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        setViewData()
//        if (ContextCompat.checkSelfPermission(context!!,
//                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationAPI?.onStart()
//        } else {
//            requestPermissions()
//        }
    }


    fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)

        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            })
        } else {

            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(view!!, getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE).setAction(activity?.getString(actionStringId), listener).show()
    }

    companion object {

        private val PICK_IMAGE_CAMERA = 101
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 102
    }


    private fun setViewData() {
        etdStateCode?.editText?.setText(if (toiletData?.stateCode == 0) "" else toiletData?.stateCode.toString())
        etdState?.editText?.setText(toiletData?.stateName)
        etdCityCode?.editText?.setText(if (toiletData?.cityCode == 0) "" else toiletData?.cityCode.toString())
        etdCity?.editText?.setText(toiletData?.cityName)
        etdAssessorName?.editText?.setText(toiletData?.assessorName)
        etdAssessorPhone?.editText?.setText(toiletData?.assessorPhoneNo)
        etdZone?.editText?.setText(toiletData?.zone)
        etdWard?.editText?.setText(toiletData?.ward)

        val categoryArray = resources.getStringArray(R.array.category)
        for (i in categoryArray.indices) {
            if (categoryArray[i].equals(toiletData?.category, ignoreCase = true)) {
                category?.setSelection(i)
                break
            }
        }

        setSelection(type, toiletData)
    }

    fun setSelection(type: Spinner?, toiletData: ToiletData?) {
        if (toiletData?.type.equals("Toilet", ignoreCase = true)) {
            type?.setSelection(1)
        } else if (toiletData?.type.equals("Urinal", ignoreCase = true)) {
            type?.setSelection(2)
        } else if (toiletData?.type.equals("Toilet And Urinal", ignoreCase = true)) {
            type?.setSelection(3)
        } else {
            type?.setSelection(0)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context?.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e("onSave", "saved")
        outState.putString(C.STATE_CODE, etdStateCode?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.stateName, etdState?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.CITY_CODE, etdCityCode?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.cityName, etdCity?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.assessorName, etdAssessorName?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.ASSESSOR_PHONE, etdAssessorPhone?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.zone, etdZone?.editText?.text.toString().trim { it <= ' ' })
        outState.putString(C.ward, etdWard?.editText?.text.toString().trim { it <= ' ' })

        outState.putInt(C.category, category?.selectedItemPosition ?: 0)
        outState.putInt(C.type, type?.selectedItemPosition ?: 0)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("onRestore", "restore")
        if (savedInstanceState != null) {
            etdStateCode?.editText?.setText(savedInstanceState.getString(C.STATE_CODE))
            etdState?.editText?.setText(savedInstanceState.getString(C.stateName))
            etdCityCode?.editText?.setText(savedInstanceState.getString(C.CITY_CODE))
            etdCity?.editText?.setText(savedInstanceState.getString(C.cityName))
            etdAssessorName?.editText?.setText(savedInstanceState.getString(C.assessorName))
            etdAssessorPhone?.editText?.setText(savedInstanceState.getString(C.ASSESSOR_PHONE))
            etdZone?.editText?.setText(savedInstanceState.getString(C.zone))
            etdWard?.editText?.setText(savedInstanceState.getString(C.ward))

            category?.setSelection(savedInstanceState.getInt(C.category))
            type?.setSelection(savedInstanceState.getInt(C.type))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(v: View) {

        if (failureCounter == 0) {
            validateEmpty(etdState);
        }
        if (failureCounter == 0) {
            validateEmpty(etdStateCode);
        }
        if (failureCounter == 0) {
            validateEmpty(etdCity);
        }
        if (failureCounter == 0) {
            validateEmpty(etdCityCode);
        }
        if (failureCounter == 0) {
            validateEmpty(etdAssessorName);
        }
        if (failureCounter == 0) {
            validateMobile(etdAssessorPhone)
        }
        if (failureCounter == 0) {
            validateEmpty(etdZone)
        }
        if (failureCounter == 0) {
            validateEmpty(etdWard)
        }
        if (failureCounter == 0) {
            validateSelection(category, toiletData)
        }
        if (failureCounter == 0) {
            validateSelection(type, toiletData)
        }


        if (failureCounter == 0) {
            onSuccess("")
        }
        failureCounter = 0
    }

    fun validateMobile(editText: TextInputLayout?) {
        val mobile = editText?.editText?.text.toString().trim { it <= ' ' }
        if (mobile.length == 0) {
            failureCounter++
            editText?.error = "Enter Mobile Number"
        } else if (mobile.length > 0 && mobile.length < 10) {
            failureCounter++
            editText?.error = "Invalid Mobile Number"
        } else {
            editText?.error = null
        }
    }


    fun validateEmpty(layout: TextInputLayout?) {
        val layoutText = layout?.editText?.text.toString().trim { it <= ' ' }
        if (layoutText.length == 0) {
            failureCounter++
            layout?.error = "Field can not be empty"
        } else {
            layout?.error = null
        }
    }

    fun validateSelection(view: View?, data: ToiletData?) {
        val position = (view as Spinner).selectedItemPosition
        if (view.getId() == R.id.category) {
            if (position == 0) {
                failureCounter++
                onFailure(ValidationError(view, "Please Select category"))
            }
        } else if (view.getId() == R.id.type) {
            if (position == 0) {
                failureCounter++
                onFailure(ValidationError(view, "Please Select type"))
            } else if (position == 1) {
                data?.type = "Toilet"
            } else if (position == 2) {
                data?.type = "Urinal"
            } else {
                data?.type = "Toilet And Urinal"
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (parent.id == R.id.type) {
            var selectedType = resources.getStringArray(R.array.type)[position]
            if (selectedType.equals("Type", ignoreCase = true)) {
                selectedType = ""
            } else {
                selectedType = if (selectedType.contains("Only"))
                    selectedType.replace("Only", "")
                else
                    selectedType
            }
            Log.e("selected spinner type", selectedType)
            toiletData?.type = selectedType
        } else if (parent.id == R.id.category) {
            Log.e("selected category", resources.getStringArray(R.array.category)[position])
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    fun onSuccess(str: String) {
        try {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val iBinder = view?.windowToken
            if (iBinder != null) {
                imm.hideSoftInputFromWindow(iBinder, 0)
                view?.clearFocus()
            }
        } catch (e: NullPointerException) {
            Log.e("onSuccess", " NullPointer")
        }


        toiletData?.assessorName = etdAssessorName?.editText?.text.toString().trim { it <= ' ' }
        toiletData?.assessorPhoneNo = etdAssessorPhone?.editText?.text.toString().trim { it <= ' ' }
        toiletData?.zone = etdZone?.editText?.text.toString().trim { it <= ' ' }
        toiletData?.ward = etdWard?.editText?.text.toString().trim { it <= ' ' }

        toiletData?.category = resources.getStringArray(R.array.category)[category?.selectedItemPosition
                ?: 0]
        Log.e("selected type", toiletData?.type + "")
        mListener?.onFragmentInteraction("2")
    }

    fun onFailure(error: ValidationError) {
        val view = error.view
        if (view is TextInputLayout) {
            view.error = error.error
        } else {
            Snackbar.make(getView()!!, error.error, Snackbar.LENGTH_SHORT).show()
            return
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101 && resultCode == Activity.RESULT_CANCELED) {
//            if (locationAPI.dialog.isShowing) {
//                locationAPI.dialog.dismiss()
//            }
//        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.size <= 0) {
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                locationAPI?.onStart()
//            } else {
//                Log.e("TAG", "Permission denied")
//                showSnackbar(R.string.permission_denied_explanation,
//                        R.string.settings, View.OnClickListener {
//                    // Build intent that displays the App settings screen.
//                    val intent = Intent()
//                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                    val uri = Uri.fromParts("package",
//                            BuildConfig.APPLICATION_ID, null)
//                    intent.data = uri
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                })
//            }
//        }
//    }

}
