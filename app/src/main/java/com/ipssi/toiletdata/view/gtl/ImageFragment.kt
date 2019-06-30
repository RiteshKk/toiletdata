package com.ipssi.toiletdata.view.gtl

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.crashlytics.android.Crashlytics
import com.ipssi.toiletdata.BuildConfig
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.ToiletLocatorApp
import com.ipssi.toiletdata.location.LocationAPI
import com.ipssi.toiletdata.location.OnLocationChangeCallBack
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.ToiletData
import com.ipssi.toiletdata.util.Utils
import com.ipssi.toiletdata.events.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_image.*

import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImageFragment : Fragment(), View.OnClickListener, OnLocationChangeCallBack {
    private var mListener: OnFragmentInteractionListener? = null

    private var modelData: ToiletData? = null
    private var bitmap1: Bitmap? = null
    private var bitmap2: Bitmap? = null
    private var bitmap3: Bitmap? = null

    private var address1: TextInputLayout? = null
    private var address2: TextInputLayout? = null
    private var pin: TextInputLayout? = null

    private var activeImage: Int = 0
    private var locationAPI: LocationAPI? = null
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var progressDialog: ProgressDialog? = null
    private var alertDialog: android.support.v7.app.AlertDialog? = null
    private var btnUpload: TextView? = null

    internal var imageToString = arrayOfNulls<String>(3)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationAPI = LocationAPI(activity, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressDialog = ProgressDialog(activity)
        progressDialog?.setMessage("Uploading data to server\nPlease wait...")
        progressDialog?.setCancelable(false)

        modelData = arguments?.getParcelable(C.DATA_TAG)

        val image1 = view.findViewById<View>(R.id.image1) as ImageView
        image1.setOnClickListener(this)
        val image2 = view.findViewById<View>(R.id.image2) as ImageView
        image2.setOnClickListener(this)
        val image3 = view.findViewById<View>(R.id.image3) as ImageView
        image3.setOnClickListener(this)
        (view.findViewById<ImageView>(R.id.btn_back)).setOnClickListener(this)

        address1 = view.findViewById(R.id.edt_toilet_address_1)
        address1?.hint = Html.fromHtml(getString(R.string.toilet_address1))
        address2 = view.findViewById(R.id.edt_toilet_address_2)
        address2?.hint = Html.fromHtml(getString(R.string.toilet_address2))
        pin = view.findViewById(R.id.edt_pin_code)
        pin?.hint = Html.fromHtml(getString(R.string.pin_code))


        (view.findViewById<View>(R.id.app_version) as TextView).text = Utils.getVersionName(activity)

        Log.e("lat = $latitude", "longitude $longitude")
        if (bitmap1 != null) {
            image1.setImageBitmap(bitmap1)
        }

        if (bitmap2 != null) {
            image2.setImageBitmap(bitmap2)
        }

        if (bitmap3 != null) {
            image3.setImageBitmap(bitmap3)
        }


        (view.findViewById<TextView>(R.id.btn_delete1)).setOnClickListener(this)
        (view.findViewById<TextView>(R.id.btn_delete2)).setOnClickListener(this)
        (view.findViewById<TextView>(R.id.btn_delete3)).setOnClickListener(this)

        btnUpload = view.findViewById(R.id.btn_upload)
        btnUpload?.setOnClickListener(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context?.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    //    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(C.address1, address1?.editText?.text.toString().trim { it <= ' ' })
//        outState.putString(C.address2, address2?.editText?.text.toString().trim { it <= ' ' })
//        outState.putString(C.pinCode, pin?.editText?.text.toString().trim { it <= ' ' })
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        address1?.editText?.setText(savedInstanceState?.getString(C.address1))
//        address2?.editText?.setText(savedInstanceState?.getString(C.address2))
//        pin?.editText?.setText(savedInstanceState?.getString(C.pinCode))
//    }
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.image1 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context!!,
                                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        activeImage = 1
                        dispatchTakePictureIntent()
                    } else {
                        activeImage = 1
                        requestPermissions()
                    }
                } else {
                    activeImage = 1
                    dispatchTakePictureIntent()
                }
            }
            R.id.image2 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context!!,
                                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        activeImage = 2
                        dispatchTakePictureIntent()
                    } else {
                        requestPermissions()
                    }
                } else {
                    activeImage = 2
                    dispatchTakePictureIntent()
                }
            }
            R.id.image3 -> {

                activeImage = 3
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context!!,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity!!,
                                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        locationAPI?.onStart()
                    } else {
                        requestPermissions()
                    }
                } else {
                    locationAPI?.onStart()
                }
            }
            R.id.btn_delete1 -> {
                val image1 = view?.findViewById<View>(R.id.image1) as ImageView
                bitmap1 = null
                image1.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_camera))
            }
            R.id.btn_delete2 -> {
                val image2 = view?.findViewById<View>(R.id.image2) as ImageView
                bitmap2 = null
                image2.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_camera))
            }
            R.id.btn_delete3 -> {
                bitmap3 = null
                val image3 = view?.findViewById<View>(R.id.image3) as ImageView
                image3.setImageDrawable(resources.getDrawable(android.R.drawable.ic_menu_camera))
            }
            R.id.btn_upload -> {

                if (bitmap1 == null || bitmap2 == null || bitmap3 == null) {
                    Snackbar.make(view!!, "Click all 3 pictures", Snackbar.LENGTH_LONG).show()
                    return
                }
                if (!validateEmpty(address1)) {
                    return
                }
                if (!validateEmpty(address2)) {
                    return
                }
                if (!validatePin(pin)) {
                    return
                }
                modelData?.toiletAddress = address1?.editText?.text.toString().trim { it <= ' ' } + "\n" + address2?.editText?.text.toString().trim { it <= ' ' }
                modelData?.pinCode = pin?.editText?.text.toString().trim { it <= ' ' }


                locationAPI?.onStop()
                v.isEnabled = false
                uploadData()
            }
            R.id.btn_back -> mListener?.onFragmentInteraction("2")
        }
    }

    fun validatePin(pin: TextInputLayout?): Boolean {
        val pinText = pin?.editText?.text.toString().trim { it <= ' ' }
        if (pinText.length == 0) {
            pin?.error = "Enter PIN"
            return false
        } else if (pinText.length < 6) {
            pin?.error = "PIN must be 6 digit"
            return false
        } else {
            pin?.error = null
            return true
        }
    }


    fun validateEmpty(layout: TextInputLayout?): Boolean {
        val layoutText = layout?.editText?.text.toString().trim { it <= ' ' }
        if (layoutText.length == 0) {
            layout?.error = "Field can not be empty"
            return false
        } else {
            layout?.error = null
            return true
        }
    }


    private fun uploadData() {
        progressDialog?.show()

        val params = JSONObject()
        try {
            params.put("mobile_no", activity?.getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)?.getString(C.MOBILE, ""))
            params.put("state_id", modelData?.stateId.toString())
            params.put("state_code", modelData?.stateCode.toString())
            params.put("city_id", modelData?.cityId.toString())
            params.put("city_cencus_code", modelData?.cityCode.toString())
            params.put("assessor_name", modelData?.assessorName)
            params.put("assessor_phn_no", modelData?.assessorPhoneNo)
            params.put("zone", modelData?.zone)
            params.put("ward", modelData?.ward)
            params.put("address", modelData?.toiletAddress)
            params.put("pincode", modelData?.pinCode)
            params.put("category", modelData?.category!![0].toString())
            params.put("type", modelData?.type)
            val toiletLocatedAt = modelData?.toiletLocatedAt
            params.put("owner_authority", (if (toiletLocatedAt!!.contains("Others")) toiletLocatedAt + "-" + modelData?.otherLocation else toiletLocatedAt))

            val authority = modelData?.authority
            params.put("maintainance_authority", if (authority!!.contains("Others")) authority + "-" + modelData?.otherAuthority else authority)

            params.put("care_taker_name", modelData?.careTakerName)
            params.put("care_taker_phn_no", modelData?.careTakerPhoneNo)
            params.put("open_days", modelData?.openingDaysString)
            params.put("opening_time", modelData?.openingTime)
            params.put("closing_time", modelData?.closingTime)
            params.put("seats", modelData?.seats.toString())
            params.put("gender", modelData?.gender)
            params.put("child_friendly", if (modelData!!.isChildFriendly) "yes" else "No")
            params.put("differently_abled_friendly", if (modelData!!.isDifferentlyAbledFriendly) "yes" else "No")
            params.put("availability_of_sanitory_napkin_machine", if (modelData!!.isNapkinMachine) "yes" else "No")
            params.put("availability_of_incinerator", if (modelData!!.isIncinerator) "yes" else "No")
            params.put("availablity_of_water_atm", if (modelData!!.isWaterAtm) "yes" else "No")
            params.put("fee", modelData?.fee)
            params.put("cost", modelData?.cost.toString())
            params.put("image_url", imageToString[0])
            params.put("image_url_two", imageToString[1])
            params.put("image_url_three", imageToString[2])

            params.put("latitude", latitude.toString())
            params.put("longitude", longitude.toString())
        } catch (e: JSONException) {
            Log.e("JSON Exception", e.message)
        }

        Log.e("JSON REQUEST", params.toString())
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()
        val URL = "http://sbmtoilet.org/backend/web/index.php?r=api/user/save"
        Crashlytics.log(1, "post param", params.toString() + "")
        val request = JsonObjectRequest(Request.Method.POST, URL, params,
                Response.Listener { response ->
                    progressDialog?.dismiss()
                    Crashlytics.log("ImageFragment->response : " + response.toString())
                    Log.e("image response", response.toString())
                    if (response.toString().length > 10) {
                        val builder = android.support.v7.app.AlertDialog.Builder(activity!!)
                        val view = LayoutInflater.from(activity)
                                .inflate(R.layout.upload_popup, null, false)
                        val tvId = view.findViewById<TextView>(R.id.popup_toilet_id)

                        val status = view.findViewById<TextView>(R.id.popup_msz)
                        val createdOn = view.findViewById<TextView>(R.id.popup_created_on)
                        val statusMsz = response.optString("status")
                        status.text = statusMsz
                        if (statusMsz.toLowerCase(Locale.getDefault()).contains("successfully")) {
                            val toilet_id = response.optString("toilet_id")
                            tvId.text = "Your Toilet Id : $toilet_id"
                            val created_at = response.optString("created_at")
                            createdOn.text = "Created At : $created_at"
                        }
                        (view.findViewById<View>(R.id.popup_btn_ok) as TextView).setOnClickListener {
                            btnUpload?.isEnabled = true
                            alertDialog?.dismiss()
                            if (statusMsz.toLowerCase(Locale.getDefault()).contains("successfully")) {
                                mListener?.onFragmentInteraction("main")
                            }
                        }
                        builder.setView(view)
                        alertDialog = builder.show()
                    } else {
                        Toast.makeText(activity, "Unexpected response", Toast.LENGTH_SHORT).show()
                        btnUpload?.isEnabled = true
                    }
                }, Response.ErrorListener { error ->
            progressDialog?.dismiss()
            btnUpload?.isEnabled = true
            val view = view
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
        request.setShouldRetryServerErrors(true)
        request.retryPolicy = DefaultRetryPolicy(3000, 2, 0f)
        request.setShouldCache(false)
        request.tag = "Image"
        requestQueue?.add(request)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationAPI?.onActivityResult(requestCode, resultCode, data)
        var bitmap: Bitmap? = null
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_CAMERA) {
                if (resultCode == Activity.RESULT_OK) {
                    setPic()
                } else {
                    Toast.makeText(context, "Unable to capture image! Please try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationAPI?.onStop()
        ToiletLocatorApp.instance?.getRequestQueue()?.cancelAll("Image")
    }

    override fun onLocationChange(loc: Location) {
        latitude = loc.latitude
        longitude = loc.longitude
        val geocoder = Geocoder(this.activity, Locale.getDefault());
        val addresses: List<Address> = geocoder.getFromLocation(loc!!.latitude, loc.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val addressObj = addresses.get(0)
        val address = addressObj.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val postalCode = addressObj.getPostalCode()
        address1?.editText?.setText("${address}")
        pin?.editText?.setText("${postalCode}")
        dispatchTakePictureIntent()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions()
                }
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationAPI?.onStart()
            } else {
                Log.e("TAG", "Permission denied")
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, View.OnClickListener {
                    // Build intent that displays the App settings screen.
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
        val shouldCameraRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.CAMERA)

        if (shouldProvideRationale && shouldCameraRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            })
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
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


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context?.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri? = context?.let { it1 ->
                        FileProvider.getUriForFile(
                                it1,
                                "com.example.android.fileprovider",
                                it
                        )
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA)
                }
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        var targetW = 0
        var targetH = 0
        if (activeImage == 1) {
            targetW = image1.width
            targetH = image1.height
        } else if (activeImage == 2) {
            targetW = image2.width
            targetH = image2.height
        } else {
            targetW = image3.width
            targetH = image3.height
        }

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)//photoW/targetW and photoH/targetH

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            if (activeImage == 1) {
                bitmap1 = bitmap
                (view?.findViewById<View>(R.id.image1) as ImageView).setImageBitmap(bitmap1)
                val baos1 = ByteArrayOutputStream()
                bitmap1?.compress(Bitmap.CompressFormat.JPEG, 100, baos1)
                val b1 = baos1.toByteArray()
                imageToString[0] = Base64.encodeToString(b1, Base64.DEFAULT)
            } else if (activeImage == 2) {
                bitmap2 = bitmap
                (view?.findViewById<View>(R.id.image2) as ImageView).setImageBitmap(bitmap)
                val baos1 = ByteArrayOutputStream()
                bitmap2?.compress(Bitmap.CompressFormat.JPEG, 100, baos1)
                val b1 = baos1.toByteArray()
                imageToString[1] = Base64.encodeToString(b1, Base64.DEFAULT)
            } else {
                bitmap3 = Utils.drawTextToBitmap(activity!!, bitmap!!, "lat:$latitude\nlng:$longitude")
                //                ((TextView) getView().findViewById(R.id.location)).setText("Latitude : " + latitude + "\n" + "Longitude : " + longitude);
                (view?.findViewById<View>(R.id.image3) as ImageView).setImageBitmap(bitmap3)
                val baos1 = ByteArrayOutputStream()
                bitmap3?.compress(Bitmap.CompressFormat.JPEG, 100, baos1)
                val b1 = baos1.toByteArray()
                imageToString[2] = Base64.encodeToString(b1, Base64.DEFAULT)

            }

//            if (activeImage == 1) {
//                image1.setImageBitmap(drawTextToBitmap)
//                image1.setBackgroundColor(resources.getColor(android.R.color.transparent))
//            } else if (activeImage == 2) {
//                image2.setImageBitmap(drawTextToBitmap)
//                image2.setBackgroundColor(resources.getColor(android.R.color.transparent))
//            } else {
//                image3.setImageBitmap(drawTextToBitmap)
//                image3.setBackgroundColor(resources.getColor(android.R.color.transparent))
//            }
        }
    }

}
