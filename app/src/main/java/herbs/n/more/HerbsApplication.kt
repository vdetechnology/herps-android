package herbs.n.more

import android.app.Application
import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.NetworkConnectionInterceptor
import herbs.n.more.data.repositories.BestSellingRepository
import herbs.n.more.data.repositories.CartRepository
import herbs.n.more.data.repositories.DetailProductRepository
import herbs.n.more.data.repositories.UserRepository
import herbs.n.more.ui.auth.AuthViewModelFactory
import herbs.n.more.ui.cart.CartViewModelFactory
import herbs.n.more.ui.detail.DetailViewModelFactory
import herbs.n.more.ui.home.BestSellingViewModelFactory
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class HerbsApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@HerbsApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }

        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton { BestSellingRepository(instance(), instance()) }
        bind() from singleton { DetailProductRepository(instance(), instance()) }
        bind() from singleton { CartRepository(instance(), instance()) }

        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { BestSellingViewModelFactory(instance()) }
        bind() from singleton { DetailViewModelFactory(instance()) }
        bind() from singleton { CartViewModelFactory(instance()) }

    }

}