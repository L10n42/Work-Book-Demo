package com.kappdev.wordbook.core.presentation.ads

import com.kappdev.wordbook.BuildConfig

enum class AdUnitId(val id: String) {
    Flashcards(BuildConfig.AD_ID_FLASHCARDS),
    Tests(BuildConfig.AD_ID_TESTS),
    Writing(BuildConfig.AD_ID_WRITING),
    ShareCard(BuildConfig.AD_ID_SHARE_CARD),
    ShareCollectionPDF(BuildConfig.AD_ID_SHARE_COLLECTION_PDF),
    ShareCollection(BuildConfig.AD_ID_SHARE_COLLECTION),
    ImportCollection(BuildConfig.AD_ID_IMPORT_COLLECTION),
    CreateBackup(BuildConfig.AD_ID_CREATE_BACKUP),
    RestoreBackup(BuildConfig.AD_ID_RESTORE_BACKUP),
}