package herbs.n.more.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import herbs.n.more.data.repositories.SavedRepository

@Suppress("UNCHECKED_CAST")
class SavedViewModelFactory(
    private val repository: SavedRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SavedViewModel(repository) as T
    }
}