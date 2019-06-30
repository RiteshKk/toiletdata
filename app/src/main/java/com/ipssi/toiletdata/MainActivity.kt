package com.ipssi.toiletdata

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.crashlytics.android.Crashlytics
import com.ipssi.toiletdata.events.OnFragmentInteractionListener
import com.ipssi.toiletdata.model.C
import com.ipssi.toiletdata.model.ToiletData
import com.ipssi.toiletdata.view.ctpt.CTPTList
import com.ipssi.toiletdata.view.gtl.GlobalDetailsFragment
import com.ipssi.toiletdata.view.gtl.ImageFragment
import com.ipssi.toiletdata.view.gtl.ListActivity
import com.ipssi.toiletdata.view.gtl.LocalDetailsFragment
import com.ipssi.toiletdata.view.gvpCityAdmin.CityAdminActivity
import com.ipssi.toiletdata.view.validation.LoginFragment
import com.ipssi.toiletdata.view.validation.OTPFragment

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {

    private var toiletData: ToiletData? = null
    private var locator: SharedPreferences? = null
    private var loginFragment: LoginFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setTitle(R.string.gtl)
        toiletData = ToiletData()
        locator = getSharedPreferences(C.PREF_NAME, Context.MODE_PRIVATE)

        loginFragment = LoginFragment()
        if (locator?.getBoolean(C.IS_LOGGED_IN, false) ?: false) {
            val role = locator?.getString(C.ROLE, "")
            if (role.equals(C.ULB, true)) {
                init()
                onFragmentInteraction("1")
            } else if (role.equals(C.CTPT, true)) {
                onFragmentInteraction(C.CTPT)
            } else if (role.equals(C.THIRD_PARTY, ignoreCase = true)) {
                onFragmentInteraction(C.THIRD_PARTY)
            } else if (role.equals(C.GVP_ADMIN, ignoreCase = true)) {
                onFragmentInteraction(C.GVP_ADMIN)
            }else if(role.equals(C.NODAL_OFFICER,true)){
                onFragmentInteraction(C.NODAL_OFFICER)
            } else {
                onFragmentInteraction("0")
            }
        } else {
            onFragmentInteraction("0")
        }
    }

    override fun onResume() {
        super.onResume()
        if (locator?.getBoolean(C.IS_LOGGED_IN, false) ?: false) {
            checkVersion()
        } else {
            onFragmentInteraction("0")
        }
    }


    fun checkVersion() {
        val url = "https://raw.githubusercontent.com/sushilrajput/demo/master/json_files/document.json"
        val requestQueue = ToiletLocatorApp.instance?.getRequestQueue()
        val request = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            val version = response?.optDouble("version", 0.0)
            val packageInfo: PackageInfo?
            try {
                packageInfo = packageManager?.getPackageInfo(packageName, 0)
                val versionName = packageInfo?.versionName?.toDouble()

                if (version != null && versionName != null && version > versionName) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Update your app to use new features")
                    builder.setTitle("Update App")
                    builder.setCancelable(false)
                    builder.setPositiveButton("Update") { view, which -> downloadApp() }
                    builder.show()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            Log.e("error check", error.message + "")
        })
        requestQueue?.add(request)
    }

    private fun downloadApp() {
        val url = "https://github.com/sushilrajput/demo/blob/master/json_files/gtl.apk?raw=true"
        //val url = "http://sbmtoilet.org/gtlApp/gtl-app-download.php"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }


    private fun init() {
        toiletData?.cityName = locator?.getString(C.cityName, "")
        toiletData?.cityId = locator?.getInt("cityId", 0) ?: 0
        toiletData?.cityCode = locator?.getInt("cityCode", 0) ?: 0
        toiletData?.stateName = locator?.getString(C.stateName, "")
        toiletData?.stateId = locator?.getInt("stateId", 0) ?: 0
        toiletData?.stateCode = locator?.getInt("stateCode", 0) ?: 0
        Crashlytics.setUserIdentifier(locator?.getString(C.MOBILE, ""))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            locator?.edit()?.putBoolean(C.IS_LOGGED_IN, false)?.apply()
            onFragmentInteraction("0")
        } else {
            startActivity(Intent(this, ListActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentInteraction(uri: String) {

        if (uri.equals("0", ignoreCase = true)) {
            toiletData = ToiletData()
            supportActionBar?.hide()

            val bundle = Bundle()
            bundle.putParcelable(C.DATA_TAG, toiletData)
            loginFragment?.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.container, loginFragment)
                    .commit()

        } else if (uri.equals("1", ignoreCase = true)) {
            init()
            supportFragmentManager.popBackStack("", FragmentManager.POP_BACK_STACK_INCLUSIVE)

            var globalDetailsFragment: GlobalDetailsFragment? = supportFragmentManager.findFragmentByTag("one") as? GlobalDetailsFragment
            if (globalDetailsFragment == null) {
                globalDetailsFragment = GlobalDetailsFragment()
            } else {
                supportFragmentManager.popBackStack("one", 0)
            }

            val bundle = Bundle()
            supportActionBar?.show()
            bundle.putParcelable(C.DATA_TAG, toiletData)
            globalDetailsFragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("one")
                    .replace(R.id.container, globalDetailsFragment, "one")
                    .commit()

        } else if (uri.equals("2", ignoreCase = true)) {
            var localDetailsFragment: LocalDetailsFragment? = supportFragmentManager.findFragmentByTag("two") as? LocalDetailsFragment
            if (localDetailsFragment == null) {
                localDetailsFragment = LocalDetailsFragment()

            } else {
                supportFragmentManager.popBackStack("two", 0)
            }

            val bundle = Bundle()
            bundle.putParcelable(C.DATA_TAG, toiletData)
            localDetailsFragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("two")
                    .replace(R.id.container, localDetailsFragment, "two")
                    .commit()

        } else if (uri.equals("3", ignoreCase = true)) {
            var imageFragment: ImageFragment? = supportFragmentManager.findFragmentByTag("three") as? ImageFragment
            if (imageFragment == null) {
                imageFragment = ImageFragment()

            } else {
                supportFragmentManager.popBackStack("three", 0)
            }

            val bundle = Bundle()
            bundle.putParcelable(C.DATA_TAG, toiletData)
            imageFragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("three")
                    .replace(R.id.container, imageFragment, "three")
                    .commit()

        } else if (uri.equals("main", ignoreCase = true)) {
            toiletData = ToiletData()
            init()
            supportFragmentManager.popBackStack("one", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val globalDetailsFragment = GlobalDetailsFragment()
            val bundle = Bundle()
            supportActionBar?.show()
            bundle.putParcelable(C.DATA_TAG, toiletData)
            globalDetailsFragment.arguments = bundle
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("one")
                    .replace(R.id.container, globalDetailsFragment, "one")
                    .commit()

        } else if (uri.equals("otp", ignoreCase = true)) {
            supportFragmentManager
                    .beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.container, OTPFragment())
                    .commit()

        } else if (uri.equals(C.CTPT, true)) {
            val intent = Intent(this, CTPTList::class.java)
            intent.putExtra("type", C.CTPT)
            startActivityForResult(intent, 102)
        } else if (uri.equals(C.THIRD_PARTY, ignoreCase = true)) {
            val intent = Intent(this, CTPTList::class.java)
            intent.putExtra("type", C.THIRD_PARTY)
            startActivityForResult(intent, 102)
        } else if (uri.equals(C.GVP_ADMIN, ignoreCase = true)) {
            val intent = Intent(this, CityAdminActivity::class.java)
            startActivityForResult(intent, 102)
        }else if(uri.equals(C.NODAL_OFFICER,true)){
            var intent= Intent(this,CityAdminActivity::class.java)
            startActivityForResult(intent,102)
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 102)
            finish()
    }
}
