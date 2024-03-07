package base

interface Config {
    
    val viewModelStore : ViewModelStore
    
}

class ConfigImpl(val vmStore: ViewModelStore) : Config {

    override val viewModelStore: ViewModelStore
        get() = vmStore


}