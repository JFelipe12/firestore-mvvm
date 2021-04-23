package com.felipedeveloper.shareplacesapp

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.felipedeveloper.shareplacesapp.data.repository.local.PreferencesRepository
import com.google.firebase.FirebaseApp
import com.squareup.picasso.Picasso
import dagger.hilt.android.HiltAndroidApp

val PREFERENCES_REPOSITORY: PreferencesRepository by lazy { App.prefs!! }

@HiltAndroidApp
class App : MultiDexApplication() {

    companion object {
        var prefs: PreferencesRepository? = null

        lateinit var appContext : Context
    }

    override fun onCreate() {
        super.onCreate()

        prefs = PreferencesRepository(applicationContext)

        FirebaseApp.initializeApp(this)

        appContext = applicationContext


        try {
            Picasso.setSingletonInstance(Picasso.Builder(applicationContext).build())
        } catch (e: IllegalStateException) {
            Log.e("picasso-instance", e.message.toString())
        }
    }

}