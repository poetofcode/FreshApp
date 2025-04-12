package com.poetofcode.freshapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

internal class AndroidImageUtil {

    @Composable
    fun AsyncImage(
        url: String,
        contentScale: ContentScale,
        modifier: Modifier,
    ) {
        AsyncImage(model = url, contentDescription = null, modifier = modifier, contentScale = contentScale)
    }
}