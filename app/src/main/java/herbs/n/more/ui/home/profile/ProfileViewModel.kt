package herbs.n.more.ui.home.profile

import androidx.lifecycle.ViewModel
import herbs.n.more.data.repositories.UserRepository

class ProfileViewModel(
    repository: UserRepository
) : ViewModel() {

    val user = repository.getUser()
}