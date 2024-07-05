package com.kappdev.wordbook.core.presentation.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kappdev.wordbook.core.presentation.navigation.NavConst.CARD_ID
import com.kappdev.wordbook.core.presentation.navigation.NavConst.COLLECTION_ID
import com.kappdev.wordbook.core.presentation.navigation.NavConst.IMAGE_URI
import com.kappdev.wordbook.main_feature.presentation.add_edit_card.components.AddEditCardScreen
import com.kappdev.wordbook.main_feature.presentation.add_edit_collection.components.AddEditCollectionScreen
import com.kappdev.wordbook.main_feature.presentation.cards.components.CardsScreen
import com.kappdev.wordbook.main_feature.presentation.collections.components.CollectionsScreen
import com.kappdev.wordbook.settings_feature.presentations.components.SettingsScreen
import com.kappdev.wordbook.study_feature.presentation.flashcards.components.FlashcardsScreen
import com.kappdev.wordbook.study_feature.presentation.tests.components.TestsScreen
import com.kappdev.wordbook.study_feature.presentation.writing.components.WritingScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    NavHost(
        navController = navController,
        startDestination = Screen.Collections.route
    ) {
        composable(
            Screen.Collections.route,
            enterTransition = {
                when {
                    navigatingFromAny(Screen.Cards, Screen.Settings, Screen.AddEditCard, Screen.AddEditCollection) -> {
                        if (isRtl) slideInRight() else slideInLeft()
                    }
                    else -> null
                }
            },
            exitTransition = {
                when {
                    navigatingTowardsAny(Screen.Cards, Screen.Settings, Screen.AddEditCard, Screen.AddEditCollection) -> {
                        if (isRtl) slideOutRight() else slideOutLeft()
                    }
                    else -> null
                }
            },
            popExitTransition = { if (isRtl) slideOutLeft() else slideOutRight() },
            popEnterTransition = {
                when {
                    navigatingFromAny(Screen.Cards, Screen.Settings, Screen.AddEditCard, Screen.AddEditCollection) -> {
                        if (isRtl) slideInLeft() else slideInRight()
                    }
                    else -> null
                }
            }
        ) {
            CollectionsScreen(navController)
        }

        slidableComposable(
            route = Screen.Cards.route + "$COLLECTION_ID={$COLLECTION_ID}",
            isRtl = isRtl,
            arguments = listOf(
                navArgument(COLLECTION_ID) { type = NavType.IntType; defaultValue = -1 }
            )
        ) {
            val collectionId = it.arguments?.getInt(COLLECTION_ID)
            CardsScreen(navController, collectionId)
        }

        slidableComposable(
            route = Screen.AddEditCollection.route + "?$COLLECTION_ID={$COLLECTION_ID}&$IMAGE_URI={$IMAGE_URI}",
            isRtl = isRtl,
            arguments = listOf(
                navArgument(COLLECTION_ID) { type = NavType.IntType; defaultValue = -1 },
                navArgument(IMAGE_URI) { type = NavType.StringType; nullable = true }
            )
        ) {
            val collectionId = it.arguments?.getInt(COLLECTION_ID)
            val imageUri = it.arguments?.getString(IMAGE_URI)?.let(Uri::parse)
            AddEditCollectionScreen(collectionId, imageUri, navController)
        }

        slidableComposable(
            route = Screen.AddEditCard.route + "?$COLLECTION_ID={$COLLECTION_ID}&$CARD_ID={$CARD_ID}&$IMAGE_URI={$IMAGE_URI}",
            isRtl = isRtl,
            arguments = listOf(
                navArgument(COLLECTION_ID) { type = NavType.IntType; defaultValue = -1 },
                navArgument(CARD_ID) { type = NavType.IntType; defaultValue = -1 },
                navArgument(IMAGE_URI) { type = NavType.StringType; nullable = true }
            )
        ) {
            val collectionId = it.arguments?.getInt(COLLECTION_ID)
            val cardId = it.arguments?.getInt(CARD_ID)
            val imageUri = it.arguments?.getString(IMAGE_URI)?.let(Uri::parse)
            AddEditCardScreen(navController, collectionId, cardId, imageUri)
        }

        slidableComposable(Screen.Settings.route, isRtl = isRtl) {
            SettingsScreen(navController)
        }

        studyComposable(Screen.Flashcards) { collectionId ->
            FlashcardsScreen(navController, collectionId)
        }

        studyComposable(Screen.Tests) { collectionId ->
            TestsScreen(navController, collectionId)
        }

        studyComposable(Screen.Writing) { collectionId ->
            WritingScreen(navController, collectionId)
        }
    }
}


/**
 * Add the [Composable] to the [NavGraphBuilder] with horizontal slide animations
 *
 * @param route route for the destination
 * @param arguments list of arguments to associate with destination
 * @param deepLinks list of deep links to associate with the destinations
 * @param content composable for the destination
 */
private fun NavGraphBuilder.slidableComposable(
    route: String,
    isRtl: Boolean,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route, arguments =  arguments, deepLinks = deepLinks, content = content,
    enterTransition = { if (isRtl) slideInRight() else slideInLeft() },
    exitTransition = { if (isRtl) slideOutRight() else slideOutLeft() },
    popExitTransition = { if (isRtl) slideOutLeft() else slideOutRight() },
    popEnterTransition = { if (isRtl) slideInLeft() else slideInRight() }
)

private fun NavGraphBuilder.studyComposable(
    screen: Screen,
    content: @Composable AnimatedContentScope.(collectionId: Int?) -> Unit
) = composable(
    route = screen.route + "?$COLLECTION_ID={$COLLECTION_ID}",
    arguments = listOf(
        navArgument(COLLECTION_ID) { type = NavType.IntType; defaultValue = -1 }
    ),
    enterTransition = { popIn() },
    exitTransition = { popOut() },
    content = {
        val collectionId = it.arguments?.getInt(COLLECTION_ID)
        content(collectionId)
    }
)