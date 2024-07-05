package com.kappdev.wordbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.firebase.analytics.FirebaseAnalytics
import com.kappdev.wordbook.analytics.data.repository.FirebaseAnalyticsHelper
import com.kappdev.wordbook.analytics.domain.repository.AnalyticsHelper
import com.kappdev.wordbook.analytics.domain.repository.LocalAnalyticsHelper
import com.kappdev.wordbook.core.domain.repository.FirstAppLaunchRecorder
import com.kappdev.wordbook.core.presentation.common.CountdownSnackbar
import com.kappdev.wordbook.core.presentation.common.LoadingDialog
import com.kappdev.wordbook.core.presentation.navigation.NavConst
import com.kappdev.wordbook.core.presentation.navigation.NavGraph
import com.kappdev.wordbook.core.presentation.navigation.Screen
import com.kappdev.wordbook.core.presentation.navigation.putArg
import com.kappdev.wordbook.main_feature.domain.repository.SampleDataProvider
import com.kappdev.wordbook.main_feature.presentation.common.components.ImageActionChooser
import com.kappdev.wordbook.main_feature.presentation.home_widget.QuickCardWidget
import com.kappdev.wordbook.reminders_feature.domain.AppUsageRecorder
import com.kappdev.wordbook.reminders_feature.domain.RemindersWorkManager
import com.kappdev.wordbook.settings_feature.domain.AppSettings
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings
import com.kappdev.wordbook.settings_feature.domain.Reminder
import com.kappdev.wordbook.settings_feature.domain.Settings
import com.kappdev.wordbook.settings_feature.domain.repository.SettingsRepository
import com.kappdev.wordbook.theme.WordBookTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var settings: SettingsRepository
    @Inject lateinit var appUsageRecorder: AppUsageRecorder
    @Inject lateinit var firstAppLaunchRecorder: FirstAppLaunchRecorder
    @Inject lateinit var sampleDataProvider: SampleDataProvider

    private val updateSnackbarEvent = MutableSharedFlow<Unit>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseAnalyticsHelper: AnalyticsHelper
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var navController: NavHostController
    private val remindersWorkManager = RemindersWorkManager(this)

    private var appSettings by mutableStateOf<AppSettings?>(null)
    private var isGeneratingData by mutableStateOf(false)
    private var imageUri by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForSample()
        launchAppSettingsListener()
        checkAndAskNotificationPermission()
        registerRemindersChangeListener()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalyticsHelper = FirebaseAnalyticsHelper(firebaseAnalytics)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStateUpdateListener)
        checkForAppUpdates()
        handleImageIntent(intent)

        setContent {
            appSettings?.let { validSettings ->
                WordBookTheme(validSettings.theme) {
                    CompositionLocalProvider(
                        LocalAppSettings provides validSettings,
                        LocalAnalyticsHelper provides firebaseAnalyticsHelper,
                    ) {
                        val snackbarHostState = remember { SnackbarHostState() }
                        navController = rememberNavController()
                        setupScreenTracking(navController)

                        NavGraph(navController)

                        LaunchedEffect(Unit) {
                            if (intent.isQuickCardIntent()) {
                                handleQuickCardIntent()
                            }
                        }

                        if (isGeneratingData) {
                            LoadingDialog()
                        }

                        LaunchedEffect(Unit) {
                            updateSnackbarEvent.collect {
                                val result = snackbarHostState.showSnackbar(
                                    message = getString(R.string.new_app_is_ready),
                                    duration = SnackbarDuration.Indefinite
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    appUpdateManager.completeUpdate()
                                }
                            }
                        }

                        if (imageUri != null) {
                            ImageActionChooser(
                                onDismiss = { imageUri = null; removeImageExtras() },
                                onCreateCard = {
                                    navController.navigate(
                                        Screen.AddEditCard.route.putArg(NavConst.IMAGE_URI, imageUri, true)
                                    )
                                },
                                onCreateCollection = {
                                    navController.navigate(
                                        Screen.AddEditCollection.route.putArg(NavConst.IMAGE_URI, imageUri, true)
                                    )
                                }
                            )
                        }

                        SnackbarHost(snackbarHostState) { data ->
                            CountdownSnackbar(
                                snackbarData = data,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    private val installStateUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            lifecycleScope.launch {
                updateSnackbarEvent.emit(Unit)
            }
        }
    }

    private fun checkForSample() {
        if (firstAppLaunchRecorder.isFirstLaunch()) {
            lifecycleScope.launch(Dispatchers.IO) {
                isGeneratingData = true
                sampleDataProvider.insertSampleDataIfNeed()
                isGeneratingData = false
                firstAppLaunchRecorder.markAsLaunched()
            }
        }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isFlexibleUpdateAllowed
            if (isUpdateAvailable && isUpdateAllowed) {
                val updateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE)
                appUpdateManager.startUpdateFlow(info, this, updateOptions)
            }
        }
    }

    private fun setupScreenTracking(navController: NavHostController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            destination.route?.substringBefore("/")?.let { screenName ->
                firebaseAnalyticsHelper.logEvent(
                    FirebaseAnalytics.Event.SCREEN_VIEW, FirebaseAnalytics.Param.SCREEN_NAME to screenName
                )
            }
        }
    }

    private fun launchAppSettingsListener() {
        lifecycleScope.launch {
            settings.getAppSettingsFlow().collectLatest { appSettings = it }
        }
    }

    private fun registerRemindersChangeListener() {
        lifecycleScope.launch {
            settings.getValueFlow(Settings.Reminder).collectLatest { state ->
                when (state) {
                    Reminder.Allowed -> remindersWorkManager.schedulePeriodicWork()
                    Reminder.Denied -> remindersWorkManager.cancelPeriodicWork()
                }
            }
        }
    }

    private fun checkAndAskNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 54599437)
    }

    override fun onResume() {
        super.onResume()
        appUsageRecorder.recordAppOpen()
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(installStateUpdateListener)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.isImageIntent()) handleImageIntent(intent)
        if (intent.isQuickCardIntent()) handleQuickCardIntent()
    }

    private fun handleQuickCardIntent() {
        if (this::navController.isInitialized) {
            navController.navigate(Screen.AddEditCard.route)
        }
        intent.setAction(null)
    }

    private fun Intent.isQuickCardIntent(): Boolean {
        return this.action == QuickCardWidget.QUICK_CARD_ACTION
    }

    private fun handleImageIntent(intent: Intent) {
        imageUri = intent.parseImageUri()?.toString()
    }

    private fun Intent.parseImageUri(): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        }
    }

    private fun removeImageExtras() {
        intent.removeExtra(Intent.EXTRA_STREAM)
    }

    private fun Intent.isImageIntent(): Boolean {
        val type = this.type
        return Intent.ACTION_SEND == this.action && type != null && type.startsWith("image/")
    }
}