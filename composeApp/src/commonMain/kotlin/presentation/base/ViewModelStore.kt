package presentation.base

import kotlinx.coroutines.CoroutineScope

class ViewModelStore(
    val coroutineScope: CoroutineScope,
    val vmFactories: List<ViewModelFactory<*>>
) {
    
    val viewModels: MutableMap<String, ViewModel> = mutableMapOf()

    inline fun <reified T: ViewModel> getViewModel(key: String) : T {
        if (viewModels.containsKey(key)) {
            return viewModels[key] as? T ?: throw Exception("ViewModelFactory unknown exception")
        }

        var createdVm: T? = null
        vmFactories.first { factory ->
            factory.vmTypeName == T::class.java.typeName
        }?.let { found ->
            createdVm = found.createViewModel() as T
            (createdVm as? BaseViewModel)?.setCoroutineScope(coroutineScope)
            viewModels[key] = createdVm as T
        }
        return createdVm ?: throw Exception("ViewModelFactory not found")
    }
    
    fun removeViewModel(viewModel: ViewModel) {
        // TODO
    }
    
}