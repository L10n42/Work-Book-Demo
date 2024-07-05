package com.kappdev.wordbook.main_feature.data.repository

import android.content.Context
import com.kappdev.wordbook.main_feature.domain.repository.CollectionsOrderSaver
import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder
import com.kappdev.wordbook.main_feature.domain.util.OrderType
import javax.inject.Inject

class CollectionsOrderSaverImpl @Inject constructor(
    context: Context
) : CollectionsOrderSaver {

    private val preferences = context.getSharedPreferences("collections_order", Context.MODE_PRIVATE)
    private val editor = preferences.edit()
    private val defaultOrder = CollectionsOrder.Name()

    override fun getOrder(): CollectionsOrder {
        val orderKey = preferences.getString(ORDER_KEY, defaultOrder.key)
        val typeName = preferences.getString(ORDER_TYPE, defaultOrder.type.name)

        return if (orderKey != null && typeName != null) {
            val orderType = OrderType.valueOf(typeName)
            CollectionsOrder.getByKey(orderKey, orderType)
        } else defaultOrder
    }

    override fun saveOrder(order: CollectionsOrder) {
        editor.putString(ORDER_KEY,order.key)
        editor.putString(ORDER_TYPE, order.type.name)
        editor.apply()
    }

    companion object {
        private const val ORDER_KEY = "collections_order_key"
        private const val ORDER_TYPE = "order_type"
    }
}