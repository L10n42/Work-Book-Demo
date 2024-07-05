package com.kappdev.wordbook.main_feature.domain.util

import android.net.Uri

sealed class Image(val model: String) {
    data object Empty: Image("")
    data class Stored(val path: String): Image(path)
    data class NotStored(val uri: Uri): Image(uri.toString())
    data class Deleted(val path: String): Image("")
    data class Replaced(val oldPath: String, val newUri: Uri): Image(newUri.toString())
}

fun Image.isNotEmptyOrDeleted() = (this !is Image.Empty && this !is Image.Deleted)

fun Image.delete(): Image {
    return when (this) {
        is Image.Deleted -> this
        is Image.NotStored -> Image.Empty
        is Image.Replaced -> Image.Deleted(this.oldPath)
        is Image.Stored -> Image.Deleted(this.path)
        is Image.Empty -> Image.Empty
    }
}

fun Image.update(newImageUri: Uri): Image {
    return when (this) {
        is Image.Deleted -> Image.Replaced(this.path, newImageUri)
        is Image.NotStored -> Image.NotStored(uri)
        is Image.Replaced -> Image.Replaced(this.oldPath, newImageUri)
        is Image.Stored -> Image.Replaced(this.path, newImageUri)
        is Image.Empty -> Image.NotStored(newImageUri)
    }
}