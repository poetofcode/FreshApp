package presentation.base

import ImageUtil
import presentation.base.ViewModelStore
import presentation.factories.MockRepositoryFactory
import presentation.factories.RepositoryFactory

data class Config(

    val viewModelStore : ViewModelStore,

    val repositoryFactory: RepositoryFactory,

    val imageUtil: ImageUtil 
)
