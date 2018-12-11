package com.abhishek.cachy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = "OOOOOOOO"
    private val cacheRepository = CacheRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Main) {
            val movies = getMoviesData()
            pb.gone()
            tv.visible()
            tv.text = movies?.title
        }
    }

    private suspend fun getMoviesData(): MovieData? {

        val key = object {}.javaClass.enclosingMethod?.toString()?.replace("\\s".toRegex(), "")
        val cachedData = getDataFromCache(key)
        if (cachedData != null) return cachedData

        val response = Api.apiService.getAllMovies().await()
        val movieData = response.body()?.first()
        return if (response.isSuccessful && movieData != null) {
            Log.d(TAG, "Got data form api..")
            saveDataToCache(key, movieData)
            return movieData
        } else {
            null
        }
    }

    private fun getDataFromCache(key: String?): MovieData? {
//        val key = object {}.javaClass.enclosingMethod?.toString()?.replace("\\s".toRegex(), "")
        if (key == null || !cacheRepository.isDataPresent(key)) return null
        Log.d(TAG, "Got data form cache..")
        val data = cacheRepository.getData(key, MovieData::class.java)
        Log.d(TAG, "data is --> ${data.title}")
        return data
    }

    private fun saveDataToCache(key: String?, movieData: MovieData) {
        if (key == null) {
            Log.d(TAG, "Saving to cache failed key is null")
            return
        }
        cacheRepository.saveData(key, movieData)
        Log.d(TAG, "Saving data --> ${movieData.title}")
    }
}