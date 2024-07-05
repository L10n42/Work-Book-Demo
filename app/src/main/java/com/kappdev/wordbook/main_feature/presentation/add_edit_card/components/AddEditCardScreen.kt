package com.kappdev.wordbook.main_feature.presentation.add_edit_card.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kappdev.wordbook.R
import com.kappdev.wordbook.analytics.domain.repository.LocalAnalyticsHelper
import com.kappdev.wordbook.analytics.domain.util.logDBAction
import com.kappdev.wordbook.core.domain.util.SnackbarStateHandler
import com.kappdev.wordbook.core.presentation.common.DuplicationAlertSheet
import com.kappdev.wordbook.core.presentation.common.FABPadding
import com.kappdev.wordbook.core.presentation.common.InputField
import com.kappdev.wordbook.core.presentation.common.LoadingDialog
import com.kappdev.wordbook.core.presentation.common.SimpleTopAppBar
import com.kappdev.wordbook.core.presentation.common.UnsavedChangesSheet
import com.kappdev.wordbook.core.presentation.common.VerticalSpace
import com.kappdev.wordbook.main_feature.domain.util.CropImageInput
import com.kappdev.wordbook.main_feature.domain.util.rememberCropImageLauncher
import com.kappdev.wordbook.main_feature.domain.util.rememberPickAndCropImageLauncher
import com.kappdev.wordbook.main_feature.domain.util.rememberTakeAndCropImageLauncher
import com.kappdev.wordbook.main_feature.presentation.add_edit_card.AddEditCardSheet
import com.kappdev.wordbook.main_feature.presentation.add_edit_card.AddEditCardViewModel
import com.kappdev.wordbook.main_feature.presentation.common.ImageSource
import com.kappdev.wordbook.main_feature.presentation.common.ImageType
import com.kappdev.wordbook.main_feature.presentation.common.components.AnimatedFAB
import com.kappdev.wordbook.main_feature.presentation.common.components.ImageCard
import com.kappdev.wordbook.main_feature.presentation.common.components.ImageUrlSheet
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings

@Composable
fun AddEditCardScreen(
    navController: NavHostController,
    collectionId: Int? = null,
    cardId: Int? = null,
    imageUri: Uri? = null,
    viewModel: AddEditCardViewModel = hiltViewModel()
) {
    val settings = LocalAppSettings.current
    val analyticsHelper = LocalAnalyticsHelper.current
    val scrollState = rememberScrollState()
    val termFocusRequester = remember { FocusRequester() }

    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarStateHandler(viewModel.snackbarState, snackbarHostState)
    LoadingDialog(viewModel.loadingDialog)

    LaunchedEffect(Unit) {
        viewModel.getCollections()
        when {
            (cardId != null && cardId > 0) -> viewModel.getCard(cardId)
            (collectionId != null && collectionId > 0) -> viewModel.getCollection(collectionId)
        }
    }

    val takeAndCropImage = rememberTakeAndCropImageLauncher(onResult = viewModel::handleCropImageResult)
    val pickAndCropImage = rememberPickAndCropImageLauncher(onResult = viewModel::handleCropImageResult)
    val cropImage = rememberCropImageLauncher(onResult = viewModel::handleCropImageResult)

    LaunchedEffect(Unit) {
        imageUri?.let { uri ->
            cropImage.launch(CropImageInput(uri, ImageType.Card))
        }
    }

    val (sheetState, openSheet) = remember { mutableStateOf<AddEditCardSheet?>(null) }
    val closeSheet = { openSheet(null) }

    SheetsManager(sheetState, viewModel, navController, closeSheet)

    var showUrlSheet by remember { mutableStateOf(false) }
    if (showUrlSheet) {
        ImageUrlSheet(
            onDismiss = { showUrlSheet = false },
            onDownload = { imageUrl ->
                viewModel.downloadImageFromUrl(imageUrl) { uri ->
                    cropImage.launch(CropImageInput(uri, ImageType.Card))
                }
            }
        )
    }

    BackHandler {
        when {
            viewModel.hasUnsavedChanges() -> openSheet(AddEditCardSheet.UnsavedChanges)
            else -> navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = when {
                    (cardId != null && cardId > 0) -> stringResource(R.string.edit_card)
                    else -> stringResource(R.string.new_card)
                },
                isElevated = scrollState.canScrollBackward,
                onBack = {
                    when {
                        viewModel.hasUnsavedChanges() -> openSheet(AddEditCardSheet.UnsavedChanges)
                        else -> navController.popBackStack()
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                fun checkForDuplicatesAndSave(onSave: () -> Unit) {
                    viewModel.findDuplicates { duplicates ->
                        if (duplicates.isNotEmpty()) {
                            openSheet(AddEditCardSheet.TermDuplication(duplicates, onSave))
                        } else {
                            onSave()
                        }
                    }
                }

                AnimatedFAB(text = stringResource(R.string.next), icon = Icons.Rounded.Redo) {
                    val saveAndAnother = {
                        viewModel.saveAndAnother {
                            analyticsHelper.logDBAction("Card created")
                            termFocusRequester.requestFocus()
                        }
                    }
                    when {
                        settings.checkForDuplication -> checkForDuplicatesAndSave(saveAndAnother)
                        else -> saveAndAnother()
                    }
                }
                AnimatedFAB(text = stringResource(R.string.done), icon = Icons.Rounded.TaskAlt) {
                    val save = {
                        viewModel.saveCard {
                            analyticsHelper.logDBAction("Card created")
                            navController.popBackStack()
                        }
                    }
                    when {
                        settings.checkForDuplication -> checkForDuplicatesAndSave(save)
                        else -> save()
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(scrollState)
                .padding(bottom = FABPadding, top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            val focusManager = LocalFocusManager.current

            CollectionChooser(
                selected = viewModel.selectedCollection,
                collections = viewModel.collections,
                onChange = viewModel::selectCollection,
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpace(16.dp)

            InputField(
                value = viewModel.term,
                onValueChange = viewModel::updateTerm,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = stringResource(R.string.term),
                hint = stringResource(R.string.enter_term),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(termFocusRequester)
            )

            VerticalSpace(16.dp)

            InputField(
                value = viewModel.transcription,
                onValueChange = viewModel::updateTranscription,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = stringResource(R.string.transcription),
                hint = stringResource(R.string.enter_transcription),
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpace(16.dp)

            InputField(
                value = viewModel.definition,
                onValueChange = viewModel::updateDefinition,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = stringResource(R.string.definition),
                hint = stringResource(R.string.enter_definition),
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpace(16.dp)

            InputField(
                value = viewModel.example,
                onValueChange = viewModel::updateExample,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                label = stringResource(R.string.example),
                hint = stringResource(R.string.enter_example),
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpace(16.dp)

            ImageCard(
                image = viewModel.cardImage,
                title = stringResource(R.string.image),
                onDelete = viewModel::deleteImage,
                modifier = Modifier.fillMaxWidth(),
                onPick = { imageSource ->
                    when (imageSource) {
                        ImageSource.Camera -> takeAndCropImage.launch(ImageType.Card)
                        ImageSource.Gallery -> pickAndCropImage.launch(ImageType.Card)
                        ImageSource.Internet -> showUrlSheet = true
                    }
                }
            )
        }
    }
}

@Composable
private fun SheetsManager(
    sheet: AddEditCardSheet?,
    viewModel: AddEditCardViewModel,
    navController: NavHostController,
    onHide: () -> Unit
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    when (sheet) {
        is AddEditCardSheet.TermDuplication -> {
            DuplicationAlertSheet(
                duplicates = sheet.duplicates,
                onDismiss = onHide,
                onCancel = onHide,
                onCreate = sheet.onSave
            )
        }

        AddEditCardSheet.UnsavedChanges -> {
            UnsavedChangesSheet(
                onDismiss = onHide,
                onDiscard = { navController.popBackStack() },
                onSave = {
                    viewModel.saveCard {
                        analyticsHelper.logDBAction("Card created")
                        navController.popBackStack()
                    }
                }
            )
        }

        null -> { /* NOTHING */ }
    }
}