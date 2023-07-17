package com.meet.project.oneclickshop.network.models

data class AddProductResponse(
    var message: String? = null,
    var product_details: ProductDetail? = null,
    var product_id: String? = null,
    var success: Boolean? = true,
)