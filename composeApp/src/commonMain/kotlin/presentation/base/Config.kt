package presentation.base

import presentation.factories.RepositoryFactory

data class Config(

    val viewModelStore : ViewModelStore,

    val repositoryFactory: RepositoryFactory,

)
