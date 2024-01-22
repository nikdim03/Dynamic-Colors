package com.example.dynamiccolors

import android.app.Application
import android.util.Log
import com.google.android.material.color.DynamicColors

/**
 * @author Дмитрий Никулин on 1/19/24
 */
class MyApp: Application() {
    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this) // to use DynamicColors in all activities
    }

    companion object {
        private const val TAG = "MyApp"
    }
}
