package com.kappdev.wordbook.main_feature.presentation.collections.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kappdev.wordbook.R
import com.kappdev.wordbook.analytics.domain.repository.LocalAnalyticsHelper
import com.kappdev.wordbook.analytics.domain.util.logButtonClick
import com.kappdev.wordbook.analytics.domain.util.logShare
import com.kappdev.wordbook.core.domain.util.SnackbarStateHandler
import com.kappdev.wordbook.core.presentation.ads.AdManager
import com.kappdev.wordbook.core.presentation.ads.AdUnitId
import com.kappdev.wordbook.core.presentation.common.AlertSheet
import com.kappdev.wordbook.core.presentation.common.FABPadding
import com.kappdev.wordbook.core.presentation.common.LoadingDialog
import com.kappdev.wordbook.core.presentation.navigation.NavConst
import com.kappdev.wordbook.core.presentation.navigation.Screen
import com.kappdev.wordbook.core.presentation.navigation.putArg
import com.kappdev.wordbook.core.presentation.util.shareContent
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionSheet
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsState.Empty
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsState.EmptySearch
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsState.Idle
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsState.Loading
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsState.Ready
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsState.Searching
import com.kappdev.wordbook.main_feature.presentation.collections.CollectionsViewModel
import com.kappdev.wordbook.main_feature.presentation.common.Option
import com.kappdev.wordbook.main_feature.presentation.common.components.AnimatedFAB
import com.kappdev.wordbook.main_feature.presentation.common.components.EmptyScreen
import com.kappdev.wordbook.main_feature.presentation.common.components.EmptySearchResult

@Composable
fun CollectionsScreen(
    navController: NavHostController,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val adManager = remember { AdManager(context) }
    val listState = rememberLazyListState()
    val (collectionSheet, openSheet) = remember { mutableStateOf<CollectionSheet?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarStateHandler(viewModel.snackbarState, snackbarHostState)

    if (collectionSheet != null) {
        CollectionSheetHandler(collectionSheet, viewModel, adManager, navController::navigate, openSheet)
    }

    LoadingDialog(viewModel.loadingDialog)

    if (adManager.isAdLoading && !viewModel.loadingDialog.isVisible.value) {
        LoadingDialog()
    }

    LaunchedEffect(Unit) {
        viewModel.getCollections()
    }

    Scaffold(
        topBar = {
            CollectionsTopBar(
                searchArg = viewModel.searchArg,
                showDivider = listState.canScrollBackward,
                orderSheetOpened = collectionSheet is CollectionSheet.Order,
                openOrder = { openSheet(CollectionSheet.Order) },
                openSettings = { navController.navigate(Screen.Settings.route) },
                onSearchValueChanged = viewModel::pendingSearch
            )
        },
        floatingActionButton = {
            AnimatedFAB(text = stringResource(R.string.btn_new), icon = Icons.Rounded.Add) {
                navController.navigate(Screen.AddEditCollection.route)
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        AnimatedContent(
            targetState = viewModel.collectionsState,
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            transitionSpec = {
                fadeIn(tween(durationMillis = 300, delayMillis = 100)) togetherWith
                        fadeOut(tween(durationMillis = 300))
            },
            label = "CollectionsStateTransition"
        ) { state ->
            when (state) {
                Idle -> {}
                Loading -> CollectionsLoading()
                Searching -> CollectionsLoading()
                EmptySearch -> EmptySearchResult()
                Empty -> EmptyScreen(stringResource(R.string.no_collections_yet))
                Ready -> {
                    CollectionsContent(
                        viewModel = viewModel,
                        listState = listState,
                        navigate = navController::navigate,
                        openSheet = openSheet
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionsContent(
    viewModel: CollectionsViewModel,
    listState: LazyListState,
    navigate: (route: String) -> Unit,
    openSheet: (newSheet: CollectionSheet?) -> Unit
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    LaunchedEffect(viewModel.collections) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = FABPadding, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(viewModel.collections, { it.id }) { collectionInfo ->

            CollectionCard(
                info = collectionInfo,
                modifier = Modifier.fillMaxWidth(),
                onNewCard = {
                    navigate(
                        Screen.AddEditCard.route.putArg(NavConst.COLLECTION_ID, collectionInfo.id, true)
                    )
                },
                onClick = {
                    navigate(
                        Screen.Cards.route.putArg(NavConst.COLLECTION_ID, collectionInfo.id)
                    )
                },
                onMore = {
                    analyticsHelper.logButtonClick("collection-more")
                    openSheet(CollectionSheet.Options(collectionInfo))
                }
            )
        }
    }
}

@Composable
private fun CollectionSheetHandler(
    sheet: CollectionSheet,
    viewModel: CollectionsViewModel,
    adManager: AdManager,
    navigate: (route: String) -> Unit,
    openSheet: (newSheet: CollectionSheet?) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val analyticsHelper = LocalAnalyticsHelper.current
    val context = LocalContext.current
    val hideSheet = { openSheet(null) }

    when (sheet) {
        is CollectionSheet.Delete -> {
            AlertSheet(
                title = stringResource(R.string.delete_collection),
                message = stringResource(R.string.delete_collection_msg),
                positive = stringResource(R.string.delete),
                onDismiss = hideSheet,
                onPositive = {
                    viewModel.deleteCollection(sheet.collection.id)
                }
            )
        }
        is CollectionSheet.Options -> {
            fun navigateWithCollection(screen: Screen) {
                navigate(screen.route.putArg(NavConst.COLLECTION_ID, sheet.collection.id, true))
            }

            fun showAdAndNavigate(screen: Screen, adUnitId: AdUnitId) {
                adManager.loadAndShowAd(adUnitId) {
                    navigateWithCollection(screen)
                }
            }

            CollectionOptions(
                collectionName = sheet.collection.name,
                onDismiss = hideSheet,
                onClick = { option ->
                    when (option) {
                        is Option.Flashcards -> if (sheet.collection.cardsCount > 0) {
                            showAdAndNavigate(Screen.Flashcards, AdUnitId.Flashcards)
                        } else viewModel.showSnackbar(R.string.at_least_1_card_error)

                        is Option.Writing -> if (sheet.collection.cardsCount > 0) {
                            showAdAndNavigate(Screen.Writing, AdUnitId.Writing)
                        } else viewModel.showSnackbar(R.string.at_least_1_card_error)

                        is Option.Tests -> if (sheet.collection.cardsCount > 3) {
                            showAdAndNavigate(Screen.Tests, AdUnitId.Tests)
                        } else viewModel.showSnackbar(R.string.at_least_4_cards_error)

                        is Option.Edit -> navigateWithCollection(Screen.AddEditCollection)
                        is Option.Delete -> openSheet(CollectionSheet.Delete(sheet.collection))

                        is Option.ShareCollection -> {
                            viewModel.shareCollection(sheet.collection.id) {
                                adManager.loadAndShowAd(AdUnitId.ShareCollection) {
                                    context.shareContent(it, type = "application/zip")
                                    analyticsHelper.logShare("Collection as zip")
                                }
                            }
                        }
                        is Option.ShareAsPDF -> {
                            viewModel.shareCollectionPdf(sheet.collection.id, layoutDirection) {
                                adManager.loadAndShowAd(AdUnitId.ShareCollectionPDF) {
                                    context.shareContent(it, type = "application/pdf")
                                    analyticsHelper.logShare("Collection as pdf")
                                }
                            }
                        }
                        else -> { /* TODO */ }
                    }
                }
            )
        }
        CollectionSheet.Order -> CollectionsOrderSheet(
            order = viewModel.order,
            onDismiss = hideSheet,
            onOrderChanged = viewModel::updateOrder
        )
    }
}