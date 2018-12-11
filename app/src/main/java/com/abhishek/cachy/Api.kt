package com.abhishek.cachy

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object Api {

    val apiService = Retrofit.Builder()
        .baseUrl("https://ghibliapi.herokuapp.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(ApiService::class.java)


    interface ApiService {
        @GET("films/")
        fun getAllMovies(): Deferred<Response<List<MovieData>>>
    }
}