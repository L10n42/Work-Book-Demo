package com.kappdev.wordbook.main_feature.data.repository

import android.content.Context
import com.kappdev.wordbook.main_feature.domain.repository.CardsOrderSaver
import com.kappdev.wordbook.main_feature.domain.util.CardsOrder
import com.kappdev.wordbook.main_feature.domain.util.OrderType
import javax.inject.Inject

class CardsOrderSaverImpl @Inject constructor(
    context: Context
) : CardsOrderSaver {

    private val preferences = context.getSharedPreferences("cards_order", Context.MODE_PRIVATE)
    private val editor = preferences.edit()
    private val defaultOrder = CardsOrder.Term()

    override fun getOrder(): CardsOrder {
        val orderKey = preferences.getString(ORDER_KEY, defaultOrder.key)
        val typeName = preferences.getString(ORDER_TYPE, defaultOrder.type.name)

        return if (orderKey != null && typeName != null) {
            val orderType = OrderType.valueOf(typeName)
            CardsOrder.getByKey(orderKey, orderType)
        } else defaultOrder
    }

    override fun saveOrder(order: CardsOrder) {
        editor.putString(ORDER_KEY,order.key)
        editor.putString(ORDER_TYPE, order.type.name)
        editor.apply()
    }

    companion object {
        private const val ORDER_KEY = "cards_order_key"
        private const val ORDER_TYPE = "order_type"
    }
}