package herbs.n.more.data.network.responses

import herbs.n.more.data.db.entities.User

data class AuthResponse(
    var code: Int,
    var success: Boolean,
    var message: String,
    var data : User
)