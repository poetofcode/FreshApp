package com.poetofcode.freshapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.MockRepositoryFactory
import presentation.factories.viewModelFactories


class MainActivity : ComponentActivity() {
    val repositoryFactory = MockRepositoryFactory()

    val vmStoreImpl = ViewModelStore(
        coroutineScope = lifecycleScope,
        vmFactories = viewModelFactories(
            repositoryFactory = repositoryFactory
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                Config(
                    viewModelStore = vmStoreImpl,
                    repositoryFactory = repositoryFactory,
                )
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//
//    val vmStoreImpl = ViewModelStore(
//        coroutineScope = rememberCoroutineScope(),
//        vmFactories = viewModelFactories
//    )
//    App(Config(viewModelStore = vmStoreImpl))
}