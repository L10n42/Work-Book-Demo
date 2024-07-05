package com.kappdev.wordbook.main_feature.presentation.cards.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kappdev.wordbook.R
import com.kappdev.wordbook.analytics.domain.repository.LocalAnalyticsHelper
import com.kappdev.wordbook.analytics.domain.util.logButtonClick
import com.kappdev.wordbook.core.presentation.common.AlertSheet
import com.kappdev.wordbook.core.presentation.common.FABPadding
import com.kappdev.wordbook.core.presentation.navigation.NavConst
import com.kappdev.wordbook.core.presentation.navigation.Screen
import com.kappdev.wordbook.core.presentation.navigation.putArg
import com.kappdev.wordbook.main_feature.presentation.cards.CardSheet
import com.kappdev.wordbook.main_feature.presentation.cards.CardsState.Empty
import com.kappdev.wordbook.main_feature.presentation.cards.CardsState.EmptySearch
import com.kappdev.wordbook.main_feature.presentation.cards.CardsState.Idle
import com.kappdev.wordbook.main_feature.presentation.cards.CardsState.Loading
import com.kappdev.wordbook.main_feature.presentation.cards.CardsState.Ready
import com.kappdev.wordbook.main_feature.presentation.cards.CardsState.Searching
import com.kappdev.wordbook.main_feature.presentation.cards.CardsViewModel
import com.kappdev.wordbook.main_feature.presentation.common.Option
import com.kappdev.wordbook.main_feature.presentation.common.components.AnimatedFAB
import com.kappdev.wordbook.main_feature.presentation.common.components.CollectionsSheet
import com.kappdev.wordbook.main_feature.presentation.common.components.EmptyScreen
import com.kappdev.wordbook.main_feature.presentation.common.components.EmptySearchResult
import com.kappdev.wordbook.share_feature.presentation.card_share.components.ShareCardSheet

@Composable
fun CardsScreen(
    navController: NavHostController,
    collectionId: Int?,
    viewModel: CardsViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val (cardSheet, openSheet) = remember { mutableStateOf<CardSheet?>(null) }

    if (cardSheet != null) {
        CardSheetHandler(cardSheet, viewModel, navController::navigate, openSheet)
    }

    LaunchedEffect(Unit) {
        when {
            (collectionId != null && collectionId > 0) -> {
                viewModel.setCollectionId(collectionId)
                viewModel.refreshCards()
                viewModel.getCollectionInfo(collectionId)
            }
            else -> navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CardsTopBar(
                collectionName = viewModel.collectionName ?: "",
                searchValue = viewModel.searchArg,
                onSearch = viewModel::pendingSearch,
                showDivider = listState.canScrollBackward,
                optionsOpened = (cardSheet == CardSheet.Order),
                navigateBack = navController::popBackStack,
                openOptions = {
                    openSheet(CardSheet.Order)
                }
            )
        },
        floatingActionButton = {
            AnimatedFAB(text = stringResource(R.string.btn_new), icon = Icons.Rounded.Add) {
                navController.navigate(
                    Screen.AddEditCard.route.putArg(NavConst.COLLECTION_ID, collectionId, true)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        AnimatedContent(
            targetState = viewModel.cardsState,
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv),
            transitionSpec = {
                fadeIn(tween(durationMillis = 300, delayMillis = 100)) togetherWith
                        fadeOut(tween(durationMillis = 300))
            },
            label = "CardsStateTransition"
        ) { state ->
            when (state) {
                Idle -> {}
                Loading -> CardsLoading()
                Searching -> CardsLoading()
                EmptySearch -> EmptySearchResult()
                Empty -> EmptyScreen(stringResource(R.string.no_cards_yet))
                Ready -> CardsContent(viewModel, listState, openSheet)
            }
        }
    }
}

@Composable
private fun CardsContent(
    viewModel: CardsViewModel,
    listState: LazyListState,
    openSheet: (newSheet: CardSheet?) -> Unit
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    LaunchedEffect(viewModel.cards) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = FABPadding, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(viewModel.cards, { it.id }) { card ->
            TermCard(
                card = card,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    analyticsHelper.logButtonClick("card-more")
                    openSheet(CardSheet.Options(card))
                },
                onSpeak = viewModel::speak
            )
        }
    }
}

@Composable
private fun CardSheetHandler(
    sheet: CardSheet,
    viewModel: CardsViewModel,
    navigate: (route: String) -> Unit,
    openSheet: (newSheet: CardSheet?) -> Unit
) {
    val hideSheet = { openSheet(null) }

    when (sheet) {
        is CardSheet.Delete -> {
            AlertSheet(
                title = stringResource(R.string.delete_card),
                message = stringResource(R.string.delete_card_msg),
                positive = stringResource(R.string.delete),
                onDismiss = hideSheet,
                onPositive = {
                    viewModel.deleteCard(sheet.card.id)
                }
            )
        }

        is CardSheet.Options -> {
            CardOptions(
                cardTerm = sheet.card.term,
                onDismiss = hideSheet,
                onClick = { option ->
                    when (option) {
                        is Option.Edit -> navigate(
                            Screen.AddEditCard.route.putArg(NavConst.CARD_ID, sheet.card.id, true)
                        )
                        is Option.MoveTo -> openSheet(CardSheet.PickCollection(sheet.card.id))
                        is Option.ShareAsImage -> openSheet(CardSheet.Share(sheet.card))
                        is Option.Delete -> openSheet(CardSheet.Delete(sheet.card))
                        else -> { /* TODO */ }
                    }
                }
            )
        }

        is CardSheet.Share -> {
            ShareCardSheet(card = sheet.card, onDismiss = hideSheet)
        }

        is CardSheet.PickCollection -> {
            CollectionsSheet(
                selected = null,
                collections = viewModel.collections,
                onDismiss = hideSheet,
                onSelect = { selectedCollection ->
                    viewModel.moveTo(sheet.cardId, selectedCollection.id)
                }
            )
        }

        CardSheet.Order -> {
            CardsOrderSheet(
                order = viewModel.order,
                onDismiss = hideSheet,
                onOrderChanged = viewModel::updateOrder
            )
        }
    }
}