package com.kappdev.wordbook.share_feature.domain.repository

import androidx.compose.ui.unit.LayoutDirection
import com.kappdev.wordbook.share_feature.domain.util.CollectionPDFResult

interface ShareCollectionAsPDF {

    fun createCollectionPDF(
        collectionId: Int,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr
    ): CollectionPDFResult

}