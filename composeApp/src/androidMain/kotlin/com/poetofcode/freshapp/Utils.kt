package com.poetofcode.freshapp

import ImageUtil
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

class AndroidImageUtil : ImageUtil {

    @Composable
    override fun AsyncImage(url: String) {
        AsyncImage(model = url, contentDescription = null)
    }
}