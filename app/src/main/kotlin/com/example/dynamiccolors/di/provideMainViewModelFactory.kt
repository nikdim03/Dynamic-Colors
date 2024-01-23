package com.example.dynamiccolors.di

import coil.ImageLoader
import coil.request.ImageRequest
import dagger.Module
import dagger.Provides
import javax.inject.Provider

/**
 * @author Дмитрий Никулин on 1/23/24
 */
@Module
class ViewModelModule {
    @Provides
    fun provideMainViewModelFactory(
        imageLoader: Provider<ImageLoader>,
        requestBuilder: Provider<ImageRequest.Builder>
    ): MainViewModelFactory {
        return MainViewModelFactory(imageLoader, requestBuilder)
    }
}
