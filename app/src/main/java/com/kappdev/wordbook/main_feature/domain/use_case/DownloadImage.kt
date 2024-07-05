package com.kappdev.wordbook.main_feature.domain.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import javax.inject.Inject

class DownloadImage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(url: String): Result<Uri> {
        val fileName = "downloaded_image_${System.currentTimeMillis()}"
        val imageFile = withContext(Dispatchers.IO) {
            File.createTempFile(fileName, ".jpg", context.externalCacheDir)
        }

        val result = downloadBitmapFromUrl(url)?.saveToFile(imageFile)

        return when (result) {
            is Result.Success -> Result.Success(imageFile.getUri())
            is Result.Failure -> result
            null -> Result.Failure(R.string.download_image_error)
        }
    }

    private fun File.getUri(): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", this)
    }

    private fun Bitmap.saveToFile(file: File): Result<Unit> {
        return try {
            FileOutputStream(file).use { out ->
                this.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(R.string.store_image_error)
        } catch (e: FileNotFoundException) {
            Result.Failure(R.string.image_not_found_error)
        }
    }

    private suspend fun downloadBitmapFromUrl(url: String): Bitmap? {
        return try {
            val loading = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(url).build()
            val result = (loading.execute(request) as SuccessResult).drawable

            (result as BitmapDrawable).bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}