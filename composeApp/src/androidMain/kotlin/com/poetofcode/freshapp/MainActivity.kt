package com.poetofcode.freshapp

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import presentation.base.ConfigImpl
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories


val vmStoreImpl = ViewModelStore(
    vmFactories = viewModelFactories
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(ConfigImpl(vmStore = vmStoreImpl))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(ConfigImpl(vmStore = vmStoreImpl))
}