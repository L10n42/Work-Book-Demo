package com.kappdev.wordbook.reminders_feature.domain

interface AppUsageRecorder {

    /**
     * Records current timestamp in millis as the last app open time.
     * */
    fun recordAppOpen()

    /**
     * @return The last app open time in millis.
     * */
    fun getLastAppOpen(): Long

}