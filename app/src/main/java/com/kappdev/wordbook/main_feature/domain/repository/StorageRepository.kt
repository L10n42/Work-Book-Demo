package com.kappdev.wordbook.main_feature.domain.repository

import android.net.Uri
import androidx.annotation.RawRes
import com.kappdev.wordbook.core.domain.util.Result

interface StorageRepository {

    fun storeImageFromResources(@RawRes resId: Int): Result<String>

    fun storeImage(uri: Uri): Result<String>

    fun deleteImage(path: String)
}