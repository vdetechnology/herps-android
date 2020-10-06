package herbs.n.more.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

const val CURRENT_USER_ID = 0

@Entity
data class User(
    var id: Int? = null,
    var userLogin: String? = null,
    var userNicename: String? = null,
    var email: String? = null,
    var displayName: String? = null
) : Serializable{
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
}