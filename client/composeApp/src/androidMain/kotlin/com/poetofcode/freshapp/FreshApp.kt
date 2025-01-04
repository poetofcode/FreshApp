package com.poetofcode.freshapp

import android.app.Application

class FreshApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: FreshApp
            private set
    }

}