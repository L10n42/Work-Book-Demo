package com.kappdev.wordbook.share_feature.presentation.card_share

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toAndroidRect
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun saveToCache(image: Bitmap, context: Context): Uri? {
        val fileName = "ShareCard-${System.currentTimeMillis()}.jpg"
        val imageFile = File(context.externalCacheDir, fileName)

        return try {
            FileOutputStream(imageFile).use { out ->
                image.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            context.getFileUri(imageFile)
        } catch (e: Exception) {
            null
        }
    }

    private fun Context.getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this, "${packageName}.provider", file)
    }

    fun takeSnapshot(
        view: View,
        bounds: Rect,
        source: Window = (view.context as Activity).window
    ): Bitmap? {
        try {
            val bitmap = bounds.createBitmap(Bitmap.Config.ARGB_8888)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PixelCopy.request(source, bounds.toAndroidRect(), bitmap, {}, Handler(Looper.getMainLooper()))
            } else {
                val canvas = Canvas(bitmap).apply {
                    translate(-bounds.left, -bounds.top)
                }
                view.draw(canvas)
                canvas.setBitmap(null)
            }

            return bitmap
        } catch (e: Exception) {
            return null
        }
    }

    private fun Rect.createBitmap(config: Bitmap.Config): Bitmap {
        return Bitmap.createBitmap(width.toInt(), height.toInt(), config)
    }
}
