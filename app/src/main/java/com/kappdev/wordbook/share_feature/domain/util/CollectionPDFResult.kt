package com.kappdev.wordbook.share_feature.domain.util

import android.net.Uri

sealed class CollectionPDFResult {
    data object EmptyCollection: CollectionPDFResult()
    data class Success(val uri: Uri): CollectionPDFResult()
}