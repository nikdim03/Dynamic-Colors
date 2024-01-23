package com.example.dynamiccolors.di

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * @author Дмитрий Никулин on 1/22/24
 */

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext

@Module
class AppModule(private val application: Application) {
    @Provides
    @Singleton
    @ApplicationContext
    fun provideApplicationContext(): Context = application

    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader(context)
    }
    @Provides
    fun provideImageRequestBuilder(@ApplicationContext context: Context): ImageRequest.Builder {
        return ImageRequest.Builder(context)
    }
}
