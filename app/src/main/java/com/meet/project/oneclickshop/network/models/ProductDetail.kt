package com.meet.project.oneclickshop.network.models

class ProductDetail(
    var image: String? = null,
    var price: Double = 0.0,
    var product_name: String? = null,
    var product_type: String? = null,
    var tax: Double = 0.0,
) {
    fun totalPrice(): Double {
        return if (tax == 0.0)
            price
        else
            (price + (price * (tax / 100)))
    }
}