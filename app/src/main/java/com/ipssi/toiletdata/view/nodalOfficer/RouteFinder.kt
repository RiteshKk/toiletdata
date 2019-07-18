package com.ipssi.toiletdata.view.nodalOfficer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ipssi.toiletdata.BuildConfig
import com.ipssi.toiletdata.R
import com.ipssi.toiletdata.ToiletLocatorApp
import com.ipssi.toiletdata.location.LocationAPI
import com.ipssi.toiletdata.location.OnLocationChangeCallBack
import kotlinx.android.synthetic.main.activity_city_admin_form.*
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class RouteFinder : AppCompatActivity(), OnMapReadyCallback, OnLocationChangeCallBack, SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private var isStopped: Boolean = false
    var currentDegree = 0f

    override fun onSensorChanged(event: SensorEvent?) {
        var degree = Math.round(event!!.values[0]).toFloat()
        // create a rotation animation (reverse turn degree degrees)
        var ra = RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        direction_text.setText(degree.toInt().toString() + Html.fromHtml(getString(R.string.degree)))
        compass.startAnimation(ra);
        currentDegree = -degree;
    }

    var lat = 0.0
    var lng = 0.0
    var sourceMarker: Marker? = null
    override fun onLocationChange(loc: Location?) {
        sourceMarker?.remove()
        // First, get an instance of the SensorManager

        sourceMarker = mMap.addMarker(MarkerOptions().title("You are Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)).position(LatLng(loc!!.latitude, loc?.longitude)))
        var builder = LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(destinationMarker?.getPosition());
        builder.include(sourceMarker?.getPosition());


        val bounds = builder.build();

        val width = getResources().getDisplayMetrics().widthPixels;
        val height = getResources().getDisplayMetrics().heightPixels;
        val padding = (height * 0.20).toInt(); // offset from edges of the map 10% of screen
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);


        var imageLocation: Location = Location("fused")
        imageLocation.latitude = lat
        imageLocation.longitude = lng
        if (loc?.distanceTo(imageLocation)!! <= 10f) {
            setResult(Activity.RESULT_OK)
            locationAPI.onStop()
            finish()
        }
        if (TextUtils.isEmpty(parseStatus) || parseStatus.equals("completed", true) || parseStatus.equals("failed", true))
            downloadData(getDirectionsUrl(LatLng(loc?.latitude, loc?.longitude), destination))
    }


    fun setCompass() {
        sensorManager.registerListener(this as SensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onResume() {
        super.onResume()
        setCompass()
    }



    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this as SensorEventListener)
    }

    private lateinit var mMap: GoogleMap

    private lateinit var locationAPI: LocationAPI

    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationAPI = LocationAPI(this, this)
        lat = intent.getDoubleExtra("lat", 0.0);
        lng = intent.getDoubleExtra("lng", 0.0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        setResult(Activity.RESULT_CANCELED)
        finish()
        return super.onOptionsItemSelected(item)
    }

    private lateinit var destination: LatLng


    private var destinationMarker: Marker? = null

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isMyLocationEnabled = true

        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true


        destination = LatLng(lat, lng)
        destinationMarker = mMap.addMarker(MarkerOptions().position(destination).icon(BitmapDescriptorFactory.fromResource(R.mipmap.garbage)).title("Destination"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 4f))
        mMap.addCircle(CircleOptions().center(destination).fillColor(resources.getColor(R.color.colorPrimaryTransparent)).radius(5.0).strokeColor(resources.getColor(R.color.colorPrimaryDark)).strokeWidth(3f))
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


    override fun onStop() {
        super.onStop()
        isStopped = true
        locationAPI.onStop()
    }

    override fun onStart() {
        super.onStart()
        isStopped = false
        locationAPI.onStart()
    }

    fun downloadData(url: String) {

        val volleyRequest = ToiletLocatorApp.instance?.getRequestQueue()
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->

            val optString = response.optString("error_message")
            if (TextUtils.isEmpty(optString)) {

                val parserTask = ParserTask()
                parserTask.execute(response.toString())
            }
        }, Response.ErrorListener { error ->
            Log.e("downloadData error", error.message)
        })

        volleyRequest?.add(jsonObjectRequest)
    }

    private var isMessageShow: Boolean = false

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                Log.e("response", jObject.toString());
                val parser = DataParser()
//
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            var points: ArrayList<LatLng>? = null
            var lineOptions: PolylineOptions? = null
            val markerOptions = MarkerOptions()

            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                val path = result[i]

                for (j in path.indices) {
                    val point = path[j]

                    val lat = point.get("lat")?.toDouble() ?: 0.0
                    val lng = point.get("lng")?.toDouble() ?: 0.0
                    val position = LatLng(lat, lng)

                    points!!.add(position)
                }

                lineOptions.addAll(points!!)
                lineOptions.width(12f)
                lineOptions.color(resources.getColor(R.color.colorPrimaryDark))
                lineOptions.geodesic(true)

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions)
            if (!isMessageShow) {
                isMessageShow = true;
                Snackbar.make(map.view!!, "Oops! it seems you are away from GVP. Please reach at right GVP location", Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }


    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Sensor enabled
        val sensor = "sensor=true"
        val mode = "mode=Bicycling"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=${getString(R.string.google_maps_key)}"
    }


    private var parseStatus: String = ""

    inner class DataParser {
        fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {

            val routes = ArrayList<ArrayList<HashMap<String, String>>>()
            val jRoutes: JSONArray
            var jLegs: JSONArray
            var jSteps: JSONArray
            try {
                parseStatus = "started"
                jRoutes = jObject.getJSONArray("routes")
                /** Traversing all routes  */
                for (i in 0 until jRoutes.length()) {
                    jLegs = (jRoutes.get(i) as JSONObject).getJSONArray("legs")
                    val path = ArrayList<HashMap<String, String>>()
                    /** Traversing all legs  */
                    for (j in 0 until jLegs.length()) {
                        jSteps = (jLegs.get(j) as JSONObject).getJSONArray("steps")

                        /** Traversing all steps  */
                        for (k in 0 until jSteps.length()) {
                            if (k == 0) {
                                val s = (jSteps.get(k) as JSONObject).get("html_instructions") as String
                                val distance = ((jSteps.get(k) as JSONObject).get("distance") as JSONObject).getString("text")
                                runOnUiThread {
                                    if (!isStopped)
                                        Toast.makeText(this@RouteFinder, Html.fromHtml(distance + " " + s), Toast.LENGTH_LONG).show()
                                }
                            }
                            var polyline = ""
                            polyline =
                                    ((jSteps.get(k) as JSONObject).get("polyline") as JSONObject).get("points") as String
                            val list = decodePoly(polyline)

                            /** Traversing all points  */
                            for (l in list.indices) {
                                val hm = HashMap<String, String>()
                                hm.put("lat", java.lang.Double.toString(list[l].latitude))
                                hm.put("lng", java.lang.Double.toString(list[l].longitude))
                                path.add(hm)
                            }
                        }
                        routes.add(path)
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
                parseStatus = "failed"
            } catch (e: Exception) {
                parseStatus = "failed"
            }

            parseStatus = "completed"
            return routes
        }


        /**
         * Method to decode polyline points
         * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private fun decodePoly(encoded: String): List<LatLng> {

            val poly = ArrayList<LatLng>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0

            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat

                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng

                val p = LatLng(
                        lat.toDouble() / 1E5,
                        lng.toDouble() / 1E5
                )
                poly.add(p)
            }
            return poly
        }
    }


    val REQUEST_TAKE_PHOTO = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_CANCELED) {
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


}
