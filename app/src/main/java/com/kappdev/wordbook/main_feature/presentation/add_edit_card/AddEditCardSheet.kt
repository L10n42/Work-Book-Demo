package com.kappdev.wordbook.main_feature.presentation.add_edit_card

import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate

sealed class AddEditCardSheet {
    data object UnsavedChanges: AddEditCardSheet()
    data class TermDuplication(
        val duplicates: List<TermDuplicate>,
        val onSave: () -> Unit
    ): AddEditCardSheet()
}