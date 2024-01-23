package com.example.dynamiccolors.activity

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.dynamiccolors.MyApp
import com.example.dynamiccolors.R
import com.example.dynamiccolors.databinding.ActivityMainBinding
import com.example.dynamiccolors.di.MainViewModelFactory
import com.example.dynamiccolors.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Дмитрий Никулин on 1/19/24
 */
@RequiresApi(Build.VERSION_CODES.O) // to use Bitmap.Config.HARDWARE
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var binding: ActivityMainBinding
    private lateinit var dynamicButton: Button
    private val vm by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate(savedInstanceState = $savedInstanceState)")
        (application as MyApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createDynamicButton()
        setupObservers()
    }

    private fun setupObservers() {
        Log.d(TAG, "setupObservers()")
        lifecycleScope.launch {
            vm.imageState.collect { state ->
                when (state) {
                    is MainViewModel.ImageState.Success -> {
                        binding.imagePlaceholder.removeAllViews()
                        binding.root.removeView(dynamicButton)
                        val imageView = ImageView(this@MainActivity).apply {
                            setImageDrawable(state.drawable)
                        }
                        applyDynamicColors(state.drawable.bitmap)
                        binding.imagePlaceholder.addView(imageView)
                        createDynamicButton()
                    }
                    is MainViewModel.ImageState.Error -> {
                        binding.imagePlaceholder.removeAllViews()
                        binding.imagePlaceholder.addView(ImageView(this@MainActivity).apply {
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.ic_broken_image
                                )
                            )
                        })
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun applyDynamicColors(resource: Bitmap) {
        Log.d(TAG, "applyDynamicColors(resource = $resource)")
        val bitmap = vm.getCorrectBitmap(resource)
        val dynamicColorsOptions = DynamicColorsOptions.Builder()
            .setContentBasedSource(bitmap)
            .build()
        DynamicColors.applyToActivityIfAvailable(
            this,
            dynamicColorsOptions
        )
    }

    private fun createDynamicButton() {
        Log.d(TAG, "createDynamicButton()")
        dynamicButton = MaterialButton(this).apply {
            text = BUTTON_TEXT
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            layoutParams.bottomMargin = vm.convertDpToPixel(BUTTON_BOTTOM_MARGIN, context)
            this.layoutParams = layoutParams
        }
        binding.root.addView(dynamicButton)
        dynamicButton.setOnClickListener {
            showInputDialog()
        }
        Log.d(TAG, "dynamicButton.currentTextColor = ${dynamicButton.currentTextColor}")
        Log.d(TAG, "dynamicButton.highlightColor = ${dynamicButton.highlightColor}")
    }

    private fun addProgressIndicator() {
        Log.d(TAG, "addProgressIndicator()")
        val progressIndicator = CircularProgressIndicator(this).apply { isIndeterminate = true }
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        progressIndicator.layoutParams = layoutParams
        binding.imagePlaceholder.addView(progressIndicator)
    }

    private fun showInputDialog() {
        Log.d(TAG, "showInputDialog()")
        val inputLayout = TextInputLayout(this).apply {
            isHintEnabled = true
            hint = DIALOG_HINT
        }
        val inputEditText = TextInputEditText(this)
        inputLayout.addView(inputEditText)

        MaterialAlertDialogBuilder(this)
            .setTitle(DIALOG_TITLE)
            .setView(inputLayout)
            .setPositiveButton(DIALOG_POSITIVE_BUTTON_TEXT) { _, _ ->
                addProgressIndicator()
                vm.loadImage(inputEditText.text.toString())
            }
            .setNegativeButton(DIALOG_NEGATIVE_BUTTON_TEXT, null)
            .show()
        Log.d(TAG, "inputEditText.currentTextColor = ${inputEditText.currentTextColor}")
        Log.d(TAG, "inputEditText.highlightColor = ${inputEditText.highlightColor}")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DIALOG_TITLE = "Enter Image URL"
        private const val DIALOG_HINT = "Image URL"
        private const val DIALOG_POSITIVE_BUTTON_TEXT = "Load"
        private const val DIALOG_NEGATIVE_BUTTON_TEXT = "Cancel"
        private const val BUTTON_TEXT = "+"
        private const val BUTTON_BOTTOM_MARGIN = 70f
    }
}
