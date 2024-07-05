package com.kappdev.wordbook.main_feature.domain.util

sealed class CardsOrder(
    val type: OrderType,
    val key: String
) {
    class Term(orderType: OrderType = OrderType.Ascending): CardsOrder(orderType, TERM_KEY)
    class Created(orderType: OrderType = OrderType.Ascending): CardsOrder(orderType, CREATED_KEY)
    class LastEdit(orderType: OrderType = OrderType.Ascending): CardsOrder(orderType, LAST_EDIT_KEY)

    fun copy(orderType: OrderType): CardsOrder {
        return when(this) {
            is Term -> Term(orderType)
            is Created -> Created(orderType)
            is LastEdit -> LastEdit(orderType)
        }
    }

    companion object {
        private const val TERM_KEY = "term"
        private const val CREATED_KEY = "created"
        private const val LAST_EDIT_KEY = "last_edit"

        fun getByKey(key: String, orderType: OrderType): CardsOrder {
            return when (key) {
                TERM_KEY -> Term(orderType)
                CREATED_KEY -> Created(orderType)
                LAST_EDIT_KEY -> LastEdit(orderType)
                else -> throw IllegalArgumentException("No cards order found with key = $key")
            }
        }
    }
}
