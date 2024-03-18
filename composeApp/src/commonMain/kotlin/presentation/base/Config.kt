package presentation.base

import data.repository.RepositoryFactory
import presentation.viewModelCoroutineScopeProvider

data class Config(

    val deviceType: DeviceTypes,

    val viewModelStore: ViewModelStore,

    val repositoryFactory: RepositoryFactory,

    ) {

    init {
        viewModelCoroutineScopeProvider.scope = viewModelStore.coroutineScope
    }

    enum class DeviceTypes(val isMobile: Boolean) {
        ANDROID(true),
        DESKTOP(false)
    }

}
