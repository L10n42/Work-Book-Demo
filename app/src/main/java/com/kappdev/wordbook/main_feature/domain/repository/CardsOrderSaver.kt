package com.kappdev.wordbook.main_feature.domain.repository

import com.kappdev.wordbook.main_feature.domain.util.CardsOrder

interface CardsOrderSaver {

    fun getOrder(): CardsOrder

    fun saveOrder(order: CardsOrder)

}