package presentation.base

import data.repository.RepositoryFactory

data class Config(

    val viewModelStore : ViewModelStore,

    val repositoryFactory: RepositoryFactory,

    )
