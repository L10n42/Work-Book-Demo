package com.kappdev.wordbook.main_feature.domain.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.kappdev.wordbook.R
import com.kappdev.wordbook.main_feature.presentation.common.ImageType
import com.kappdev.wordbook.theme.SkyBlue

class PickAndCropImageContract : ActivityResultContract<ImageType, CropImageView.CropResult>() {

    override fun createIntent(context: Context, input: ImageType): Intent {
        val options = generateDefaultCropImageOptions(context, input).copy(
            imageSourceIncludeCamera = false
        )
        return CropImageContract().createIntent(context, CropImageContractOptions(null, options))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CropImageView.CropResult {
        return CropImageContract().parseResult(resultCode, intent)
    }
}

@Composable
fun rememberPickAndCropImageLauncher(
    onResult: (CropImageView.CropResult) -> Unit
) = rememberLauncherForActivityResult(contract = PickAndCropImageContract(), onResult = onResult)

class TakeAndCropImageContract : ActivityResultContract<ImageType, CropImageView.CropResult>() {

    override fun createIntent(context: Context, input: ImageType): Intent {
        val options = generateDefaultCropImageOptions(context, input).copy(
            imageSourceIncludeGallery = false
        )
        return CropImageContract().createIntent(context, CropImageContractOptions(null, options))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CropImageView.CropResult {
        return CropImageContract().parseResult(resultCode, intent)
    }
}

@Composable
fun rememberTakeAndCropImageLauncher(
    onResult: (CropImageView.CropResult) -> Unit
) = rememberLauncherForActivityResult(contract = TakeAndCropImageContract(), onResult = onResult)

class CropImageContract : ActivityResultContract<CropImageInput, CropImageView.CropResult>() {

    override fun createIntent(context: Context, input: CropImageInput): Intent {
        val options = generateDefaultCropImageOptions(context, input.imageType).copy(
            imageSourceIncludeGallery = false
        )
        return CropImageContract().createIntent(context, CropImageContractOptions(input.uri, options))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CropImageView.CropResult {
        return CropImageContract().parseResult(resultCode, intent)
    }
}

@Composable
fun rememberCropImageLauncher(
    onResult: (CropImageView.CropResult) -> Unit
) = rememberLauncherForActivityResult(
    contract = com.kappdev.wordbook.main_feature.domain.util.CropImageContract(),
    onResult = onResult
)

data class CropImageInput(
    val uri: Uri,
    val imageType: ImageType
)

private fun generateDefaultCropImageOptions(
    context: Context,
    imageType: ImageType
): CropImageOptions {
    return CropImageOptions(
        fixAspectRatio = (imageType == ImageType.Card),
        allowFlipping = false,
        autoZoomEnabled = true,
        cropMenuCropButtonTitle = context.getString(R.string.done),
        activityTitle = context.getString(R.string.crop_image),
        borderLineColor = SkyBlue.toArgb(),
        borderCornerColor = SkyBlue.toArgb(),
        guidelinesColor = SkyBlue.copy(0.5f).toArgb()
    )
}
