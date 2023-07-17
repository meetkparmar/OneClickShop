package com.meet.project.oneclickshop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.meet.project.oneclickshop.R
import com.meet.project.oneclickshop.databinding.ComposeFragmentBinding
import com.meet.project.oneclickshop.utils.BaseFragment
import com.meet.project.oneclickshop.utils.getInjector

class ProductsListFragment : BaseFragment() {

    private val injector by lazy { getInjector(requireContext()) }
    private lateinit var binding: ComposeFragmentBinding
    private val viewModel: ProductViewModel by activityViewModels { injector.provideProductViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.compose_fragment, container, false)
        initData()
        binding.composeView.setContent {
            ProductsListScreen(
                viewModel = viewModel,
                addProductClick =  ::addProductClick
            )
        }

        return binding.root
    }

    private fun initData() {
        viewModel.searchedText = ""
        viewModel.productListScreenLoading = true
        viewModel.fetchProductsList(
            onSuccess = {
                if (it.isEmpty()) {
                    viewModel.errorScreenForProductList = true
                    viewModel.errorMessageForProductList = "No products available"
                } else {
                    viewModel.productsList.clear()
                    viewModel.productsList.addAll(it)
                    viewModel.searchedProductsList.clear()
                    viewModel.searchedProductsList.addAll(it)
                    viewModel.productTypeList.clear()
                    viewModel.productsList.forEachIndexed { i, detail ->
                        if (!viewModel.productTypeList.contains(detail.product_type))
                            viewModel.productTypeList.add(detail.product_type.orEmpty())
                    }
                }
                viewModel.productListScreenLoading = false

            },
            onFailure = {
                viewModel.errorScreenForProductList = true
                viewModel.errorMessageForProductList = it
                viewModel.productListScreenLoading = false
            }
        )
    }

    private fun addProductClick() {
        findNavController().navigate(R.id.action_to_add_products_fragment)
    }

    override fun onBackPressed(): Boolean {
        activity?.finish()
        return true
    }
}