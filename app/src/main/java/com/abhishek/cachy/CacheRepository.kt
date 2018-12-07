package com.abhishek.cachy

import android.content.Context
import com.google.gson.Gson


class CacheRepository {

    companion object {

        private const val NAME = "CachePref"
        val pref = MyApp.instance.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun saveData(key: String, data: Any) {
        pref.edit()
            .putString(key, Gson().toJson(data))
            .apply()
    }

    inline fun <reified T : Any> getData(key: String, t: Class<out T>): T {
        val data = pref.getString(key, null)
        return Gson().fromJson(data, t)
    }

    fun isDataPresent(key: String) = pref.contains(key)
}