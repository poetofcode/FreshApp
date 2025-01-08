package com.poetofcode.freshapp.utils

import android.content.Context
import androidx.core.app.ShareCompat

fun Context.shareLink(url: String) {
    ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setChooserTitle("Share URL")
        .setText(url)
        .startChooser()
}