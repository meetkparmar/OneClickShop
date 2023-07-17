package com.meet.project.oneclickshop.utils

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    open fun onBackPressed(): Boolean = false
}