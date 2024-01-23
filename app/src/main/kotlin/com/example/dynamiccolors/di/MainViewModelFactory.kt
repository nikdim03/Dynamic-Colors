package com.example.dynamiccolors.di

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.dynamiccolors.viewModel.MainViewModel
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Дмитрий Никулин on 1/23/24
 */
class MainViewModelFactory @Inject constructor(
    private val imageLoader: Provider<ImageLoader>,
    private val requestBuilder: Provider<ImageRequest.Builder>
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O) // to use Bitmap.Config.HARDWARE
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                imageLoader.get(),
                requestBuilder.get()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
