package com.example.dynamiccolors

import android.app.Application
import android.util.Log
import com.example.dynamiccolors.di.AppComponent
import com.example.dynamiccolors.di.AppModule
import com.example.dynamiccolors.di.DaggerAppComponent
import com.google.android.material.color.DynamicColors

/**
 * @author Дмитрий Никулин on 1/19/24
 */
class MyApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        DynamicColors.applyToActivitiesIfAvailable(this) // to use DynamicColors in all activities
    }

    companion object {
        private const val TAG = "MyApp"
    }
}
