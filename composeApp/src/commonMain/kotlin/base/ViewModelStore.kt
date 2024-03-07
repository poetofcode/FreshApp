package base

class ViewModelStore(
    val vmFactories: List<ViewModelFactory<*>>
) {
    
    inline fun <reified T: ViewModel> getViewModel(key: String) : T {
        vmFactories.first { factory ->
            TODO()
        }
        
        TODO()
    }
    
    fun removeViewModel(viewModel: ViewModel) {
        // TODO
    }
    
}