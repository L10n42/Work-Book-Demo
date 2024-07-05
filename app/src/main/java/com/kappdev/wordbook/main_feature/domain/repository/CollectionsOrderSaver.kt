package com.kappdev.wordbook.main_feature.domain.repository

import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder

interface CollectionsOrderSaver {

    fun getOrder(): CollectionsOrder

    fun saveOrder(order: CollectionsOrder)

}