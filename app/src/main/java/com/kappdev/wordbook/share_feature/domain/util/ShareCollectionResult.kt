package com.kappdev.wordbook.share_feature.domain.util

import android.net.Uri

sealed class ShareCollectionResult {
    data class Success(val uri: Uri): ShareCollectionResult()
    data object EmptyCollection: ShareCollectionResult()
    data object CollectionError: ShareCollectionResult()
}