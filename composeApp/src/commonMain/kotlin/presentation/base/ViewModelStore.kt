package base

class ViewModelStore(
    val vmFactories: List<ViewModelFactory<*>>
) {
    
    inline fun <reified T: ViewModel> getViewModel(key: String) : T {
        var createdVm: T? = null
        vmFactories.first { factory ->
            factory.vmTypeName == T::class.java.typeName
        }?.let { found ->
            createdVm = found.createViewModel() as T
        }
        return createdVm ?: throw Exception("ViewModelFactory not found")
    }
    
    fun removeViewModel(viewModel: ViewModel) {
        // TODO
    }
    
}