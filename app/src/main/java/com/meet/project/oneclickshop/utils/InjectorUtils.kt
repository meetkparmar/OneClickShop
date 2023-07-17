package com.meet.project.oneclickshop.utils

import android.content.Context
import com.meet.project.oneclickshop.ui.fragments.ProductViewModel

private lateinit var InjectorUtils: Injector

fun getInjector(context: Context): Injector {
    if (!::InjectorUtils.isInitialized) {
        InjectorUtils = Injector(context)
    }
    return InjectorUtils
}

class Injector(context: Context) {

    fun provideProductViewModel(): ProductViewModel.Factory {
        return ProductViewModel.Factory()
    }

}