package com.example.dynamiccolors.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @author Дмитрий Никулин on 1/22/24
 */
@RequiresApi(Build.VERSION_CODES.O) // to use Bitmap.Config.HARDWARE
class MainViewModel(
    private val imageLoader: ImageLoader,
    private val requestBuilder: ImageRequest.Builder
) : ViewModel() {
    private val _imageState = MutableStateFlow<ImageState>(ImageState.Idle)
    val imageState = _imageState.asStateFlow()

    fun loadImage(imageUrl: String) {
        Log.d(TAG, "loadImage(imageUrl = $imageUrl)")
        if (imageUrl.isBlank()) {
            _imageState.value = ImageState.Error(EMPTY_LINK_ERROR_MESSAGE)
            return
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val request = requestBuilder
                    .data(imageUrl)
                    .build()
                val drawable = withTimeoutOrNull(TIMEOUT_IN_MILLIS) {
                    imageLoader.execute(request).drawable
                }
                _imageState.value = when (drawable) {
                    is BitmapDrawable -> ImageState.Success(drawable)
                    null -> ImageState.Error(IMAGE_TIMEOUT_ERROR_MESSAGE)
                    else -> ImageState.Error(LINK_BROKEN_ERROR_MESSAGE)
                }
                // clean up to avoid memory leaks
                (imageLoader as? Disposable)?.dispose()
            }
        }
    }

    fun getCorrectBitmap(resource: Bitmap): Bitmap = if (resource.config == Bitmap.Config.HARDWARE) {
        Log.d(TAG, "getCorrectBitmap(resource = $resource)")
        resource.copy(ARGB_8888, true)
    } else {
        resource
    }

    fun convertDpToPixel(valueDp: Float, context: Context): Int {
        Log.d(TAG, "convertDpToPixel(valueDp = $valueDp)")
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            valueDp,
            context.resources.displayMetrics
        ).toInt()
    }

    sealed class ImageState {
        data object Idle : ImageState()
        data class Success(val drawable: BitmapDrawable) : ImageState()
        data class Error(val message: String) : ImageState()
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val IMAGE_TIMEOUT_ERROR_MESSAGE = "Image load timed out"
        private const val LINK_BROKEN_ERROR_MESSAGE = "The link is broken"
        private const val EMPTY_LINK_ERROR_MESSAGE = "URL cannot be empty"
        private const val TIMEOUT_IN_MILLIS = 10000L
    }
}
