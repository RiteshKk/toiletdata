package gov.mohua.gtl.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun drawTextToBitmap(gContext: Context,
                         imageBitmap: Bitmap,
                         gText: String): Bitmap {
        var bitmap = imageBitmap
        val resources = gContext.resources
        val scale = resources.displayMetrics.density
        var bitmapConfig: Bitmap.Config? = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)

        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D rgb(61, 61, 61)
        paint.color = Color.GREEN
        // text size in pixels
        paint.textSize = (10 * scale).toInt().toFloat()
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
        val split = gText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var totalHeight = 0
        for (i in split.indices) {
            val bounds = Rect()
            paint.getTextBounds(split[i], 0, split[i].length, bounds)
            val x = (bitmap.width - bounds.width()) / 8
            totalHeight += bounds.height()
            val y = (bitmap.height + totalHeight * 8) / 10
            canvas.drawText(split[i], x.toFloat(), y.toFloat(), paint)
        }
        return bitmap
    }


    fun drawDetailsToBitmap(gContext: Context,
                            imageBitmap: Bitmap,
                            gText: String, id: String, dateTime: String): Bitmap {
        var bitmap = imageBitmap
        val resources = gContext.resources
        val scale = resources.displayMetrics.density
        var bitmapConfig: Bitmap.Config? = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)

        val canvas = Canvas(bitmap)
        // new antialised Paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D rgb(61, 61, 61)
        paint.color = Color.GREEN
        // text size in pixels
        paint.textSize = (12 * scale).toInt().toFloat()
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
        val split = gText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var totalHeight = 20
        for (i in split.indices) {
            val bounds = Rect()
            paint.getTextBounds(split[i], 0, split[i].length, bounds)
            val x = 20
            totalHeight += bounds.height()
            val y = totalHeight
            canvas.drawText(split[i], x.toFloat(), y.toFloat(), paint)
        }

        val splitTime = dateTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var totalHeight1 = 20
        for (i in splitTime.indices) {
            val bounds = Rect()
            paint.getTextBounds(splitTime[i], 0, splitTime[i].length, bounds)
            val x = bitmap.width - (bounds.width() + 20)
            totalHeight1 += bounds.height()
            var y = totalHeight1
            if (i > 0) {
                y += 10
            }
            canvas.drawText(splitTime[i], x.toFloat(), y.toFloat(), paint)
        }

        val bounds = Rect()
        paint.getTextBounds(id, 0, id.length, bounds)
        canvas.drawText(id, 20F, (bitmap.height - (bounds.height() + 20F)), paint)

        return bitmap
    }

    fun saveImage(context: Context, b: Bitmap, imageName: String, extension: String) {
        var name = imageName
        name = "$name.$extension"
        val out: FileOutputStream
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getVersionName(activity: Activity?): String {
        val packageInfo: PackageInfo?
        try {
            packageInfo = activity?.packageManager?.getPackageInfo(activity.packageName, 0)
            return "Version : ${packageInfo?.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getImageBitmap(context: Context, imageName: String, extension: String): Bitmap? {
        var name = imageName
        name = "$name.$extension"
        try {
            val fis = context.openFileInput(name)
            val b = BitmapFactory.decodeStream(fis)
            fis.close()
            return b
        } catch (e: Exception) {
        }

        return null
    }

    const val SIMPLE_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss"

    fun getCurrentFormattedDateTime(format: String): String {
        val dateFormatter = SimpleDateFormat(format, Locale.US)
        return dateFormatter.format(Date())
    }
}
