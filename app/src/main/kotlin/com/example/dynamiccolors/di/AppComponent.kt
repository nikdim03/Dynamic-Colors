package com.example.dynamiccolors.di

import com.example.dynamiccolors.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * @author Дмитрий Никулин on 1/22/24
 */
@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}
