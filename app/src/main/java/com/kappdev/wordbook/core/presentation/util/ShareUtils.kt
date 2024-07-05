package com.kappdev.wordbook.core.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kappdev.wordbook.R

fun Context.shareContent(contentUri: Uri, type: String, title: String = getString(R.string.share_via),) {
    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
        this.type = type
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, contentUri)
    }

    val chooserIntent = Intent.createChooser(sharingIntent, title)
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(chooserIntent)
}