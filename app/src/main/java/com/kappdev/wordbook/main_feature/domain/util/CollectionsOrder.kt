package com.kappdev.wordbook.main_feature.domain.util

sealed class CollectionsOrder(
    val type: OrderType,
    val key: String
) {
    class Name(orderType: OrderType = OrderType.Ascending): CollectionsOrder(orderType, NAME_KEY)
    class Cards(orderType: OrderType = OrderType.Ascending): CollectionsOrder(orderType, CARDS_KEY)
    class Created(orderType: OrderType = OrderType.Ascending): CollectionsOrder(orderType, CREATED_KEY)
    class LastEdit(orderType: OrderType = OrderType.Ascending): CollectionsOrder(orderType, LAST_EDIT_KEY)

    fun copy(orderType: OrderType): CollectionsOrder {
        return when(this) {
            is Name -> Name(orderType)
            is Cards -> Cards(orderType)
            is Created -> Created(orderType)
            is LastEdit -> LastEdit(orderType)
        }
    }

    companion object {
        private const val NAME_KEY = "name"
        private const val CARDS_KEY = "cards_count"
        private const val CREATED_KEY = "created"
        private const val LAST_EDIT_KEY = "last_edit"

        fun getByKey(key: String, orderType: OrderType): CollectionsOrder {
            return when (key) {
                NAME_KEY -> Name(orderType)
                CARDS_KEY -> Cards(orderType)
                CREATED_KEY -> Created(orderType)
                LAST_EDIT_KEY -> LastEdit(orderType)
                else -> throw IllegalArgumentException("No collections order found with key = $key")
            }
        }
    }
}