package com.majestykapps.arch.util

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

inline fun <reified T : Fragment>
        newFragmentInstance(vararg params: Pair<String, Any>) =
    T::class.java.newInstance().apply {
        arguments = bundleOf(*params)
    }