package com.meet.project.oneclickshop.ui.fragments

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meet.project.oneclickshop.network.MainService
import com.meet.project.oneclickshop.network.RetrofitClient
import com.meet.project.oneclickshop.network.models.AddProductResponse
import com.meet.project.oneclickshop.network.models.ProductDetail
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProductViewModel() : ViewModel() {

    var productListScreenLoading by mutableStateOf(value = false)
    var errorScreenForProductList by mutableStateOf(value = false)
    var errorMessageForProductList by mutableStateOf(value = "")

    var addProductLoading by mutableStateOf(value = false)
    var addImageLoading by mutableStateOf(value = false)
    var errorScreenForAddProduct by mutableStateOf(value = false)
    var errorMessageForAddProduct by mutableStateOf(value = "")

    var productAdded by mutableStateOf(value = false)
    var imageAdded by mutableStateOf(value = false)
    var showImage by mutableStateOf(value = true)

    var productsList = mutableStateListOf<ProductDetail>()
    var productTypeList = mutableStateListOf<String>()
    var searchedProductsList = mutableStateListOf<ProductDetail>()
    var addProductResponse by mutableStateOf<AddProductResponse?>(value = null)

    private val retrofit = RetrofitClient.getInstance()
    private val apiInterface = retrofit.create(MainService::class.java)

    fun fetchProductsList(
        onSuccess: (List<ProductDetail>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiInterface.getProductsList()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    onSuccess(response.body()!!)
                } else {
                    onFailure(response.message() ?: "Something Went Wrong!")
                }
            } catch (e: Exception) {
                Log.e("fetchProductsList", e.localizedMessage)
            }
        }
    }

    private fun createPartFromString(param: String): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), param)
    }

    fun addProduct(
        onSuccess: (AddProductResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val name: RequestBody = createPartFromString(productName)
                val productType: RequestBody = createPartFromString(selectedType)
                val productPrice: RequestBody = createPartFromString(price)
                val productTaxes: RequestBody = createPartFromString(taxes)

                val map = HashMap<String, RequestBody>()
                map["product_name"] = name
                map["product_type"] = productType
                map["price"] = productPrice
                map["tax"] = productTaxes

                var file: RequestBody? = null
                var body: MultipartBody.Part? = null
                if (image != null) {
                    file = image?.let { RequestBody.create("image/jpeg".toMediaTypeOrNull(), it) }
                    body = MultipartBody.Part.createFormData("files[]", "image.jpeg", file!!)
                }

                val response = apiInterface.addProduct(partMap = map, file = body)
                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess(response.body()!!)
                } else {
                    onFailure(response.message() ?: "Something Went Wrong!")
                }
            } catch (e: Exception) {
                Log.e("addProduct", e.localizedMessage)
            }
        }
    }

    var searchedText by mutableStateOf(value = "")
    fun searchedTextChange(newText: String) {
        searchedText = newText
        searchProducts()
    }

    private fun searchProducts() {
        searchedProductsList.clear()
        if (searchedText.isEmpty()) {
            searchedProductsList.addAll(productsList)
        } else {
            productsList.forEachIndexed { i, item ->
                if (item.product_name?.contains(searchedText, true) == true)
                    searchedProductsList.add(item)
            }
        }

        if (searchedProductsList.isEmpty()) {
            errorScreenForProductList = true
            errorMessageForProductList = "No products available"
        } else {
            errorScreenForProductList = false
            errorMessageForProductList = ""
        }
    }

    var productName by mutableStateOf(value = "")
    fun productNameTextChange(newText: String) {
        if (newText.length <= 30) {
            productName = newText
        }
    }

    var expanded by mutableStateOf(false)
    var selectedType by mutableStateOf("")
    var image by mutableStateOf<File?>(value = null)

    var price by mutableStateOf(value = "")
    fun priceTextChange(newText: String) {
        if (newText.length <= 10) {
            price = newText
        }
    }

    var taxes by mutableStateOf(value = "0")
    fun taxesTextChange(newText: String) {
        if (newText.toFloat() < 100.00f) {
            taxes = newText
        }
    }

    class Factory() :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductViewModel() as T
        }
    }
}
