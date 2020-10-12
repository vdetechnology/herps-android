package herbs.n.more.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.User

@Database(
    entities = [User::class, Product::class, Cart::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getProductDao(): ProductDao
    abstract fun getCartDao(): CartDao
    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "HerbsDatabase.db"
            ).fallbackToDestructiveMigration().build()
    }
}