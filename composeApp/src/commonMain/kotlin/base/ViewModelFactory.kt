package base

interface ViewModelFactory<out T : ViewModel> {
    fun createViewModel() : T
}
