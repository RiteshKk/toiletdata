package gov.mohua.gtl

import android.app.Application
import android.text.TextUtils

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.analytics.FirebaseAnalytics

class ToiletLocatorApp : Application() {

    private var mRequestQueue: RequestQueue? = null

    fun getRequestQueue(): RequestQueue? {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return mRequestQueue
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        mRequestQueue?.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        mRequestQueue?.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        mRequestQueue?.cancelAll(tag)
    }

    companion object {
        val TAG = ToiletLocatorApp::class.java
                .simpleName

        @get:Synchronized
        var instance: ToiletLocatorApp? = null
            private set
    }
}
