package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.User

data class AuthResponse(

    var isSuccessful: Boolean?,
    var message: String?,
    var user: User?
)