package com.abhishek.cachy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

        pb.visible()
        GlobalScope.launch(Dispatchers.Main) {
            val response = Api.apiService.getAllMovies().await()
            val movieData = response.body()
            pb.gone()
            tv.visible()
            if (response.isSuccessful && movieData?.isNotEmpty() == true) {
                movieData.forEach {
                    tv.append("${it.title}\n")
                }
            } else {
                tv.text = "Api call failed ${response.code()}"
            }
        }
        /*mockApiCall(object : ResultListener<String> {
            override fun onSuccess(t: String) {
                Log.v(TAG, t)
            }

            override fun onFailure(e: Throwable) {
                Log.e(TAG, e.message)
            }
        })*/
    }

    private fun mockApiCall(resultListener: ResultListener<String>) {
        Single.defer {
            Single.just(getData())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<String> {
                override fun onSuccess(t: String) {
                    resultListener.onSuccess(t)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    resultListener.onFailure(e)
                }
            })
    }

    private fun getData(): String {
        val key = object {}.javaClass.enclosingMethod?.toString()?.replace("\\s".toRegex(), "")
        if (key != null && cacheRepository.isDataPresent(key)) {
            return cacheRepository.getData(key, String::class.java)
        }
        val data = someLongRunningTask()
        key?.let { cacheRepository.saveData(it, data) }
        return data
    }

    private fun someLongRunningTask(): String {
        Thread.sleep(4000)
        return "Hello.."
    }
}