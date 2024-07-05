package com.kappdev.wordbook.core.domain.repository

interface FirstAppLaunchRecorder {

    fun isFirstLaunch(): Boolean

    fun markAsLaunched()

}