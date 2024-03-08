package com.poetofcode.freshapp

import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

internal class AndroidImageUtil {

    @Composable
    fun AsyncImage(url: String) {
        AsyncImage(model = url, contentDescription = null)
    }
}