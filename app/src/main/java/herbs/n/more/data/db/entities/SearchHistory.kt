package herbs.n.more.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    var title: String? = null,
    var update_date: Long? = null
){
    @PrimaryKey(autoGenerate = true)
    var sid: Int = 0
}