package com.ipssi.toiletdata.view.nodalOfficer

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil.setContentView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.crashlytics.android.Crashlytics
import com.google.gson.Gson
import com.ipssi.toiletdata.BuildConfig
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.ToiletLocatorApp
import com.ipssi.toiletdata.controller.NodalOfficerWardDetailsAdapter
import com.ipssi.toiletdata.events.OnCaptureButtonClickedListener
import com.ipssi.toiletdata.location.LocationAPI
import com.ipssi.toiletdata.location.OnLocationChangeCallBack
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.ViewData
import com.ipssi.toiletdata.util.Utils
import com.ipssi.toiletdata.view.gvpCityAdmin.CityAdminForm
import com.ipssi.toiletdata.view.nodalOfficer.model.NodalDataList
import kotlinx.android.synthetic.main.activity_nodal_officer_ward_list.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class NodalOfficerWardList : AppCompatActivity(), OnCaptureButtonClickedListener, OnLocationChangeCallBack {
    var position: Int = 0

    override fun onUploadButtonClicked(data: ViewData?) {
        val URL = "http://sbmtoilet.org/backend/web/index.php?r=api/user/save-current-day-image"
        val params = JSONObject()
        progressDialog.show()
        val preference = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        params.put("mobile_no", dataList?.user_details?.mobile_no)
        params.put("role", dataList?.user_details?.role)
        params.put("state_id", viewData?.state_id)
        params.put("city_id", viewData?.city_id)
        params.put("ward", viewData?.ward_id)
        params.put("gvp_id", viewData?.gvp_id)
        params.put("nodal_gvp_id", gvpId)
        params.put("image", viewData?.imageString)
        params.put("comment", viewData?.nodalOfficerComment)

        Crashlytics.log(1, "post param", params.toString() + "")
        val volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URL, params, Response.Listener { response ->
            progressDialog.dismiss()

            Crashlytics.log("nodal officer->response : " + response.toString())
            Log.e("response", response.toString())
            if (response.toString().length > 5) {

                val status = response.optInt("status")
                val message = response.optString("message")
                Snackbar.make(parent_container, message, Snackbar.LENGTH_SHORT).show()
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

    private var viewData: ViewData? = null
    override fun onLocationChange(loc: Location?) {

        latitude = loc?.latitude
        longitude = loc?.longitude
        var imageLocation: Location = Location("fused")
        imageLocation.latitude = viewData?.latitude ?: 0.0
        imageLocation.longitude = viewData?.longitude ?: 0.0
        if (loc?.distanceTo(imageLocation)!! <= 10f) {
            dispatchTakePictureIntent()
        } else {
            val intent = Intent(this, RouteFinder::class.java)
            intent.putExtra("lat", viewData?.latitude)
            intent.putExtra("lng", viewData?.longitude)
            startActivityForResult(intent, 105)
        }
        locationAPI.onStop()
    }

    override fun onCaptureButtomClicked(viewData: ViewData?, position: Int) {
        this.viewData = viewData
        gvpId = null
        getGVPId()
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

    override fun onStop() {
        super.onStop()
        locationAPI.onStop()
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
        val targetW: Int = 500
        val targetH: Int = 500

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

        if (gvpId != null) {
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
                val drawTextToBitmap = Utils.drawDetailsToBitmap(this, bitmap, "lat:$latitude\nlng:$longitude", "Id:${gvpId}", Utils.getCurrentFormattedDateTime(Utils.SIMPLE_DATE_TIME_FORMAT))
                bitmap.recycle()
                val baos1 = ByteArrayOutputStream()
                drawTextToBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos1)
                val b1 = baos1.toByteArray()
                imageToString = Base64.encodeToString(b1, Base64.DEFAULT)
                viewData?.nodalOfficerImage = drawTextToBitmap
                viewData?.imageString = imageToString
                viewData?.nodal_clicked_img = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                openCommentBox();

            }
        } else {
            Snackbar.make(parent_container, "unable to fetch GVP ID", Snackbar.LENGTH_SHORT).show()
        }
    }


    private var gvpId: String? = null

    fun getGVPId() {
        progressDialog.show()
        val params = JSONObject()
        val preference = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        params.put("mobile_no", dataList?.user_details?.mobile_no)
        params.put("role", dataList?.user_details?.role)
        params.put("state_id", viewData?.state_id)
        params.put("city_id", viewData?.city_id)
        params.put("ward", viewData?.ward_id)
        params.put("gvp_id", viewData?.gvp_id)
        val URL = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-id-request"
        var volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
        var jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URL, params, Response.Listener { response ->
            progressDialog.dismiss()
            Crashlytics.log("gvp access->response : " + response.toString())
            Log.e("response", response.toString())
            val code = response.getString("code")
            if (code.equals("200")) {
                gvpId = response.getString("gvp_id")
            }

        }, Response.ErrorListener { error ->
            progressDialog?.dismiss()
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


    private var dialog: AlertDialog? = null

    private fun openCommentBox() {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Enter Comment")
        var view = getLayoutInflater().inflate(R.layout.comment_view, null, false)
        alertDialog.setView(view)
        var saveBtn = view.findViewById<TextView>(R.id.btn_save)
        var etView = view.findViewById<EditText>(R.id.et_comment)
        saveBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                viewData?.nodalOfficerComment = etView.text.toString()
                adapter.update(viewData, position)
                dialog?.dismiss()
                onUploadButtonClicked(viewData)
            }
        })
        dialog = alertDialog.show()
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
        } else if (requestCode == 105 && resultCode == Activity.RESULT_OK) {
            dispatchTakePictureIntent()
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

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0


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
    private lateinit var adapter: NodalOfficerWardDetailsAdapter

    private lateinit var locationAPI: LocationAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nodal_officer_ward_list)
        recycler_view.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        locationAPI = LocationAPI(this, this)
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait...")
        progressDialog?.setCancelable(false)

        adapter = NodalOfficerWardDetailsAdapter()
        recycler_view.adapter = adapter
        adapter.setListener(this)
        fetchList()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private var dataList: NodalDataList? = null

    private fun fetchList() {
        val URL = "http://sbmtoilet.org/backend/web/index.php?r=api/user/gvp-view-data"
        val params = JSONObject()
        progressDialog.show()
        val preference = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)
        params.put("mobile_no", preference?.getString(C.MOBILE, ""))
        params.put("role", preference?.getString(C.ROLE, ""))
        params.put("state_id", (preference?.getInt("stateId", 0)).toString())
        params.put("city_id", (preference?.getInt("cityId", 0)).toString())
        params.put("ward", "${intent.getIntExtra(C.SELECTED_WARD_ID, 0)}")
        Crashlytics.log(1, "post param", params.toString() + "")
        val volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URL, params, Response.Listener { response ->
            progressDialog.dismiss()

            Crashlytics.log("nodal officer->response : " + response.toString())
            Log.e("response", response.toString())
            if (response.toString().length > 5) {
                dataList = Gson().fromJson(response.toString(), NodalDataList::class.java)
                if (dataList?.status == 200)
                    if (dataList?.data != null) {
                        empty_view.visibility = View.GONE
                    } else {
                        empty_view.visibility = View.VISIBLE
                    }
                adapter.setData(dataList?.data)
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
