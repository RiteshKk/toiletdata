package com.ipssi.toiletdata.view.gvpCityAdmin

import android.Manifest
import android.app.Activity
import android.support.v7.app.AlertDialog
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
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.crashlytics.android.Crashlytics
import com.ipssi.toiletdata.BuildConfig
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.ToiletLocatorApp
import com.ipssi.toiletdata.controller.CategoryListAdapter
import com.ipssi.toiletdata.location.LocationAPI
import com.ipssi.toiletdata.location.OnLocationChangeCallBack
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.util.Utils
import kotlinx.android.synthetic.main.activity_city_admin_form.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CityAdminForm : AppCompatActivity(), View.OnClickListener, OnLocationChangeCallBack {

    private var gvpId: String? = null

    private lateinit var alertDialog: AlertDialog

    override fun onClick(v: View?) {
        if (v?.id == R.id.captureImage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    locationAPI.onStart()
                } else {
                    requestPermissions()
                }
            } else {
                locationAPI.onStart()
            }
        } else if (v?.id == R.id.removeImage) {
            val file = File(currentPhotoPath)
            if (file != null && file.exists()) {
                file.delete()
            }
            image.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            image.setImageBitmap(null)
            v.visibility = View.GONE
            captureImage.setText(R.string.capture_image)

        } else if (v?.id == R.id.btn_submit) {

            if (TextUtils.isEmpty(et_address.text.trim())) {
                Snackbar.make(parent_container, "Please Enter Address", Snackbar.LENGTH_SHORT).show()
                return
            }

            if (imageToString == null) {
                Snackbar.make(parent_container, "Please Capture Image", Snackbar.LENGTH_SHORT).show()
                return
            }

            progressDialog.show()
            val params = JSONObject()
            try {
                val preference = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
                params.put("mobile_no", preference?.getString(C.MOBILE, ""))
                params.put("role", preference?.getString(C.ROLE, ""))
                params.put("state_id", (preference?.getInt("stateId", 0)).toString())
                params.put("city_id", (preference?.getInt("cityId", 0)).toString())
                params.put("ward", intent.getIntExtra(C.SELECTED_WARD_ID, 0))
                params.put("gvp_id", gvpId)
                val innerData = JSONObject()
                innerData.put("image", imageToString)
                innerData.put("lat", latitude)
                innerData.put("lng", longitude)
                innerData.put("category", selectedCategoryId)
                innerData.put("address", et_address.text)
                innerData.put("landmark", et_landmark.text)
                innerData.put("comment", et_comment.text)

                params.put("data", innerData)
            } catch (e: JSONException) {
                Log.e("upload exception", e.message)
            }
            val URL = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-admin-post-data"
            Crashlytics.log(1, "post param", params.toString() + "")
            val volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URL, params, Response.Listener { response ->
                progressDialog.dismiss()
                Crashlytics.log("city admin form->response : " + response.toString())
                Log.e("response", response.toString())
                val statusCode = response.getString("status")
                val builder = android.support.v7.app.AlertDialog.Builder(this)
                val view = LayoutInflater.from(this)
                        .inflate(R.layout.upload_popup, null, false)
                val tvId = view.findViewById<TextView>(R.id.popup_toilet_id)

                val status = view.findViewById<TextView>(R.id.popup_msz)
                val createdOn = view.findViewById<TextView>(R.id.popup_created_on)
                status.text = if (statusCode.equals("200")) {
                    tvId.visibility = View.VISIBLE
                    tvId.text = "GVP Id : ${response.optString("gvp_id")}"
                    createdOn.text = "Created At : ${Utils.getCurrentFormattedDateTime(Utils.SIMPLE_DATE_TIME_FORMAT)}"
                    "Data Saved Successfully"
                }else {
                    tvId.visibility = View.GONE
                    "Ooops!! Something went wrong"}

                (view.findViewById<View>(R.id.popup_btn_ok) as TextView).setOnClickListener {
                    if (statusCode.equals("200")) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    alertDialog?.dismiss()
                }
                builder.setView(view)
                alertDialog = builder.show()
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissionCamera() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)

        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale_camera,
                    android.R.string.ok, View.OnClickListener {
                requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        103)
            })
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    103)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val shouldCameraRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)

        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
                        102)
            })
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA),
                    102)
        }
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(parent_container, getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE).setAction(getString(actionStringId), listener).show()
    }


    private lateinit var progressDialog: ProgressDialog

    private var selectedCategoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setContentView(R.layout.activity_city_admin_form)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        locationAPI = LocationAPI(this, this)

        gvpId = intent.getStringExtra(C.GVP_ID)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Uploading data to server\nPlease wait...")
        progressDialog?.setCancelable(false)

        val locator = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        val categoryListJson = locator.getString(C.CATEGORY_LIST, "")

        val json = JSONObject(categoryListJson)
        val keys = json.keys()
        val catList = ArrayList<android.util.Pair<Int, String>>()
        if (keys != null) {
            while (keys.hasNext()) {
                val key = keys.next()
                val value = json.optString(key)
                catList.add(android.util.Pair(key.toInt(), value))
            }
        }

        categoryList.adapter = CategoryListAdapter(this, android.R.layout.simple_spinner_dropdown_item, catList)
        categoryList.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0?.selectedItem
                val pair = selectedItem as Pair<Int, String>
                (p1 as TextView).text = pair.second
                selectedCategoryId = pair.first
            }

        })

        app_version.setText(Utils.getVersionName(this))
        selected_ward.setText(intent?.getStringExtra(C.SELECTED_WARD))
        captureImage.setOnClickListener(this)
        removeImage.visibility = View.GONE
        removeImage.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }


    val REQUEST_TAKE_PHOTO = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    if (takePictureIntent.resolveActivity(packageManager) != null)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }


    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private var imageToString: String? = null

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = image.width
        val targetH: Int = image.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            val drawTextToBitmap = Utils.drawDetailsToBitmap(this, bitmap, "lat:$latitude\nlng:$longitude", "Id:${intent.getStringExtra(C.GVP_ID)}", Utils.getCurrentFormattedDateTime(Utils.SIMPLE_DATE_TIME_FORMAT))
            bitmap.recycle()
            val baos1 = ByteArrayOutputStream()
            drawTextToBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos1)
            val b1 = baos1.toByteArray()
            imageToString = Base64.encodeToString(b1, Base64.DEFAULT)
            image.setImageBitmap(drawTextToBitmap)
            image.setBackgroundColor(resources.getColor(android.R.color.transparent))
            removeImage.visibility = View.VISIBLE
            captureImage.setText(R.string.replace_image)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                setPic()
            } else {
                Toast.makeText(this, "Unable to capture image! Please try Again", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == 101 && resultCode == Activity.RESULT_CANCELED) {
            if (locationAPI.dialog.isShowing) {
                locationAPI.dialog.dismiss()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 102) {
            if (grantResults.size <= 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions()
                }
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                locationAPI.onStart()
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
//        }else if(requestCode == 103){
//            if (grantResults.size <= 0) {
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
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
        }
    }


    private lateinit var locationAPI: LocationAPI
    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onLocationChange(loc: Location?) {
        latitude = loc?.latitude
        longitude = loc?.longitude
        val geocoder = Geocoder(this, Locale.getDefault());
        val addresses: List<Address> = geocoder.getFromLocation(loc!!.latitude, loc.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        val addressObj = addresses.get(0)
        val address = addressObj.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        et_address.setText(address)
        dispatchTakePictureIntent()
        locationAPI.onStop()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()
        locationAPI?.onStop()
        ToiletLocatorApp.instance?.getRequestQueue()?.cancelAll("Image")
    }
}
