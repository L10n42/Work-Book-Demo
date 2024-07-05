package com.kappdev.wordbook.main_feature.presentation.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
import com.kappdev.wordbook.R
import kotlin.reflect.KClass

enum class ImageSource(val icon: ImageVector, @StringRes val titleRes: Int) {
    Camera(Icons.Rounded.PhotoCamera, R.string.camera),
    Gallery(Icons.Rounded.Image, R.string.gallery),
    Internet(Icons.Rounded.Language, R.string.internet)
}

