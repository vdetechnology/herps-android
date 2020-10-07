package herbs.n.more.data.repositories

import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.User
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.responses.AuthResponse
import herbs.n.more.data.network.SafeApiRequest

class UserRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): AuthResponse{
        return apiRequest { api.userLogin(email, password) }
    }

    suspend fun userSignup(
        username: String,
        email: String,
        password: String
    ) : AuthResponse {
        return apiRequest{ api.userSignup(username, email, password)}
    }

    suspend fun userFogotPassword(
        email: String
    ) : AuthResponse {
        return apiRequest{ api.userForgotPassword(email)}
    }

    suspend fun saveUser(user: User) = db.getUserDao().upsert(user)

    fun saveUserIO(user: User) = db.getUserDao().save(user)

    fun getUser() = db.getUserDao().getuser()
}