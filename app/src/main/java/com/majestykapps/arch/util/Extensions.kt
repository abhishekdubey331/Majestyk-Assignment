package com.majestykapps.arch.util

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.net.URL

inline fun <reified T : Fragment>
        newFragmentInstance(vararg params: Pair<String, Any>) =
    T::class.java.newInstance().apply {
        arguments = bundleOf(*params)
    }

fun URL.getTaskId(): String {
    return this.path.replace("/", "")
}

fun View.changeVisibility(showView: Boolean) {
    visibility = if (showView) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
