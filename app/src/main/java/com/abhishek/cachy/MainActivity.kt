package com.abhishek.cachy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
            movies?.forEach { tv.append(it.title) }
        }
    }

    private suspend fun getMoviesData(): List<MovieData>? {
        val response = Api.apiService.getAllMovies().await()
        val movieData = response.body()
        return if (response.isSuccessful && movieData?.isNotEmpty() == true) {
            movieData
        } else {
            null
        }
    }

    private fun getData(): String {
        val key = object {}.javaClass.enclosingMethod?.toString()?.replace("\\s".toRegex(), "")
        if (key != null && cacheRepository.isDataPresent(key)) {
            return cacheRepository.getData(key, String::class.java)
        }
        val data = ""
        key?.let { cacheRepository.saveData(it, data) }
        return data
    }
}