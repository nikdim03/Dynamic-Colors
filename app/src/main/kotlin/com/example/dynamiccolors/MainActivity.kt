package com.example.dynamiccolors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.request.ImageRequest
import com.example.dynamiccolors.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * @author Дмитрий Никулин on 1/19/24
 */
@RequiresApi(Build.VERSION_CODES.O) // to use Bitmap.Config.HARDWARE
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dynamicButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate(savedInstanceState = $savedInstanceState)")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        createDynamicButton()
    }

    private fun createDynamicButton() {
        Log.d(TAG, "createDynamicButton()")
        dynamicButton = MaterialButton(this).apply {
            text = PLUS_SIGN
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            layoutParams.bottomMargin = convertDpToPixel(BUTTON_BOTTOM_MARGIN, context)
            this.layoutParams = layoutParams
            setPadding(32, 16, 32, 16)
        }
        binding.root.addView(dynamicButton)
        dynamicButton.setOnClickListener {
            showInputDialog()
        }
        println("dynamicButton.currentTextColor = ${dynamicButton.currentTextColor}")
        println("dynamicButton.highlightColor = ${dynamicButton.highlightColor}")
    }

    private fun convertDpToPixel(valueDp: Float, context: Context): Int {
        Log.d(TAG, "convertDpToPixel(valueDp = $valueDp)")
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            valueDp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun showInputDialog() {
        Log.d(TAG, "showInputDialog()")
        val inputLayout = TextInputLayout(this).apply {
            isHintEnabled = true
            hint = INPUT_HINT
        }
        val inputEditText = TextInputEditText(this)
        inputLayout.addView(inputEditText)

        MaterialAlertDialogBuilder(this)
            .setTitle(DIALOG_TEXT)
            .setView(inputLayout)
            .setPositiveButton(POSITIVE_BUTTON_TEXT) { _, _ ->
                val url = inputEditText.text.toString()
                if (url.isNotBlank()) {
                    loadAndDisplayImage(url)
                } else {
                    Toast.makeText(this, EMPTY_LINK_ERROR_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(NEGATIVE_BUTTON_TEXT, null)
            .show()
        println("inputEditText.currentTextColor = ${inputEditText.currentTextColor}")
        println("inputEditText.highlightColor = ${inputEditText.highlightColor}")
    }

    private fun loadAndDisplayImage(imageUrl: String) {
        Log.d(TAG, "loadAndDisplayImage(imageUrl = $imageUrl)")
        val progressIndicator = CircularProgressIndicator(this).apply {
            isIndeterminate = true
        }
        binding.imagePlaceholder.removeAllViews()
        binding.imagePlaceholder.addView(progressIndicator)

        val request = ImageRequest.Builder(this)
            .data(imageUrl)
            .target(
                onSuccess = { result ->
                    binding.imagePlaceholder.removeAllViews()
                    val imageView = ImageView(this).apply {
                        setImageDrawable(result)
                    }
                    if (result is BitmapDrawable) {
                        applyDynamicColors(result.bitmap)
                    }
                    binding.imagePlaceholder.addView(imageView)
                },
                onError = {
                    binding.imagePlaceholder.removeAllViews()
                    binding.imagePlaceholder.addView(ImageView(this).apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_broken_image
                            )
                        )
                    })
                    Toast.makeText(this@MainActivity, LINK_BROKEN_ERROR_MESSAGE, Toast.LENGTH_LONG).show()
                }
            )
            .build()

        val requestDisposable = coil.ImageLoader(this).enqueue(request)

        // Set up a countdown timer for timeout
        object : CountDownTimer(TIMEOUT_IN_MILLIS, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "onTick(millisUntilFinished = $millisUntilFinished)")
            }

            override fun onFinish() {
                Log.d(TAG, "onFinish()")
                if (!requestDisposable.isDisposed) {
                    requestDisposable.dispose()
                    binding.imagePlaceholder.removeAllViews()
                    binding.imagePlaceholder.addView(ImageView(this@MainActivity).apply {
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_broken_image
                            )
                        )
                    })
                    Toast.makeText(this@MainActivity, IMAGE_TIMEOUT_ERROR_MESSAGE, Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun applyDynamicColors(resource: Bitmap) {
        Log.d(TAG, "applyDynamicColors(resource = $resource)")
        val bitmap = if (resource.config == Bitmap.Config.HARDWARE) {
            resource.copy(ARGB_8888, true)
        } else {
            resource
        }
        val dynamicColorsOptions = DynamicColorsOptions.Builder()
            .setContentBasedSource(bitmap)
            .build()

        DynamicColors.applyToActivityIfAvailable(
            this,
            dynamicColorsOptions
        )
        binding.root.removeView(dynamicButton)
        createDynamicButton()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DIALOG_TEXT = "Enter Image URL"
        private const val IMAGE_TIMEOUT_ERROR_MESSAGE = "Image load timed out"
        private const val LINK_BROKEN_ERROR_MESSAGE = "The link is broken"
        private const val EMPTY_LINK_ERROR_MESSAGE = "URL cannot be empty"
        private const val INPUT_HINT = "Image URL"
        private const val POSITIVE_BUTTON_TEXT = "Load"
        private const val NEGATIVE_BUTTON_TEXT = "Cancel"
        private const val PLUS_SIGN = "+"
        private const val TIMEOUT_IN_MILLIS = 5000L
        private const val COUNTDOWN_INTERVAL = 1000L
        private const val BUTTON_BOTTOM_MARGIN = 70f
    }
}
