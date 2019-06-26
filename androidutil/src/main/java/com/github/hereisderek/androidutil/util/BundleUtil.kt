package com.github.hereisderek.androidutil.util

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import androidx.annotation.WorkerThread
import org.json.JSONException
import org.json.JSONObject

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:11
 * Project: AndroidUtil
 */


object BundleUtil {

    val Bundle?.debugString: String
        get() = this?.let {
            val keySize = keySet().size
            StringBuilder("[").apply {
                keySet().forEachIndexed { index, key ->
                    append(key).append(":").append(get(key))
                    if (index != keySize - 1) append(", ")
                }
            }.append("]").append(" size:$keySize").toString()
        } ?: "null"


    /* conversion with json */

    fun Bundle.bundleToJsonObject(): JSONObject {
        try {
            val output = JSONObject()
            for (key in this.keySet()) {
                val value = this.get(key)
                if (value is Int || value is String)
                    output.put(key, value)
                else
                    throw RuntimeException("only Integer and String can be extracted")
            }
            return output
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

    }

    fun JSONObject.JsonObjectToBundle(bundle: Bundle = Bundle()): Bundle {
        try {
            val keys = this.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                val value = this.get(key)
                if (value is String)
                    bundle.putString(key, value as String)
                else if (value is Int)
                    bundle.putInt(key, value as Int)
                else
                    throw RuntimeException("only Integer and String can be re-extracted")
            }
            return bundle
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
    }


    @WorkerThread
    fun Bundle.saveToFile(context: Context, filePath: String) {
        val bundle = this
        context.openFileOutput(filePath, Context.MODE_PRIVATE).use { fos ->
            var p : Parcel? = null
            try {
                p = Parcel.obtain()
                bundle.writeToParcel(p, 0)
                fos.write(p.marshall())
                fos.flush()
            } finally {
                p?.recycle()
            }
        }
    }

    @WorkerThread
    fun readFromFile(context: Context, filePath: String) : Bundle? {
        val byteArray = context.openFileInput(filePath).use { fis ->
            val byteArray = ByteArray(fis.channel.size().toInt())
            fis.read(byteArray, 0, byteArray.size)
            byteArray
        }
        var parcel : Parcel? = null
        try {
            parcel = Parcel.obtain()
            parcel.unmarshall(byteArray, 0, byteArray.size)
            parcel.setDataPosition(0)
            val bundle = parcel.readBundle()
            bundle?.putAll(bundle) //?? https://stackoverflow.com/questions/14256809/save-bundle-to-file
            return bundle
        } finally {
            parcel?.recycle()
        }
    }

}