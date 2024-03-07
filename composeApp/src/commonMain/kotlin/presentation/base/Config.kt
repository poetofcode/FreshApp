package presentation.base

import presentation.base.ViewModelStore
import presentation.factories.MockRepositoryFactory
import presentation.factories.RepositoryFactory

data class Config(

    val viewModelStore : ViewModelStore,

    val repositoryFactory: RepositoryFactory = MockRepositoryFactory()

)
