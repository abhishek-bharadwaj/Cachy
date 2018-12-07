package com.abhishek.cachy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val TAG = "OOOOOOOO"
    private val cacheRepository = CacheRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mockApiCall(object : ResultListener<String> {
            override fun onSuccess(t: String) {
                Log.v(TAG, t)
            }

            override fun onFailure(e: Throwable) {
                Log.e(TAG, e.message)
            }
        })
    }

    private fun mockApiCall(resultListener: ResultListener<String>) {
        val key = object {}.javaClass.enclosingMethod?.toString()?.replace("\\s".toRegex(), "")
        if (key != null && cacheRepository.isDataPresent(key)) {
            resultListener.onSuccess(cacheRepository.getData(key, String::class.java))
            return
        }
        Single.defer {
            Single.just(someLongRunningTask())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<String> {
                override fun onSuccess(t: String) {
                    resultListener.onSuccess(t)
                    key?.let {
                        cacheRepository.saveData(key, t)
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    resultListener.onFailure(e)
                }
            })
    }

    private fun someLongRunningTask(): String {
        Thread.sleep(4000)
        return "Hello.."
    }
}