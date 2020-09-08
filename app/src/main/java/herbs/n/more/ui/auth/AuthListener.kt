package herbs.n.more.ui.auth

import herbs.n.more.data.db.entities.User

interface AuthListener {

    fun onStarted()
    fun onSuccess(user: User)
    fun onFailure(message: String)
}