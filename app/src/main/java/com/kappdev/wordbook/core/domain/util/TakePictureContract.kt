package com.kappdev.wordbook.core.domain.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File

class TakePictureContract : ActivityResultContract<Unit, Uri?>() {

    private var imageUri: Uri? = null

    override fun createIntent(context: Context, input: Unit): Intent {
        val fileName = "captured_image_${System.currentTimeMillis()}"
        val imageFile = File.createTempFile(fileName, ".jpg", context.externalCacheDir)
        imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && imageUri != null) imageUri else null
    }

}

@Composable
fun rememberTakePictureLauncher(
    onResult: (Uri?) -> Unit
) = rememberLauncherForActivityResult(
    contract = TakePictureContract(),
    onResult = onResult
)