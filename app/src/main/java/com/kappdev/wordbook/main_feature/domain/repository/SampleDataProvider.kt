package com.kappdev.wordbook.main_feature.domain.repository

interface SampleDataProvider {

    /** Insert sample data into the database if it's empty. */
    suspend fun insertSampleDataIfNeed()
}