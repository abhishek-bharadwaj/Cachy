package com.abhishek.cachy

interface ResultListener<T> {
    fun onSuccess(t: T)

    fun onFailure(e: Throwable)
}