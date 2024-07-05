package com.kappdev.wordbook.share_feature.domain.repository

import android.net.Uri
import com.kappdev.wordbook.share_feature.domain.util.ImportCollectionResult
import com.kappdev.wordbook.share_feature.domain.util.ShareCollectionResult

interface ShareCollection {

    suspend fun createCollectionZip(collectionId: Int): ShareCollectionResult

    suspend fun importCollection(zipUri: Uri): ImportCollectionResult
}