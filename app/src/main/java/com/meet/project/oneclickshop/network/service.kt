package com.meet.project.oneclickshop.network

import com.meet.project.oneclickshop.network.models.AddProductResponse
import com.meet.project.oneclickshop.network.models.ProductDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface MainService {

    @GET("public/get")
    suspend fun getProductsList(): Response<List<ProductDetail>>

    @Multipart
    @JvmSuppressWildcards
    @POST("public/add")
    suspend fun addProduct(
        @PartMap partMap: Map<String, RequestBody>,
        @Part file: MultipartBody.Part?
    ): Response<AddProductResponse>

}