package com.kappdev.wordbook.core.presentation.navigation

sealed class Screen(val route: String) {
    data object Collections: Screen("collections/")
    data object Cards: Screen("cards/")
    data object AddEditCollection: Screen("add-edit-collection/")
    data object AddEditCard: Screen("add-edit-card/")

    data object Settings: Screen("settings/")

    data object Flashcards: Screen("flashcards/")
    data object Tests: Screen("tests/")
    data object Writing: Screen("writing/")
}
