package herbs.n.more.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.os.ConfigurationCompat
import herbs.n.more.R
import herbs.n.more.util.ApiException
import herbs.n.more.util.Constant
import herbs.n.more.util.NoInternetException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NetworkConnectionInterceptor(
    context: Context
) : Interceptor {

    private val applicationContext = context.applicationContext

    @RequiresApi(Build.VERSION_CODES.M)
    override fun intercept(chain: Interceptor.Chain): Response? {
        if (!isInternetAvailable())
            throw NoInternetException(applicationContext.resources.getString(R.string.network_error))

        try {
            val original: Request = chain.request()

            val currentLocale =
                ConfigurationCompat.getLocales(applicationContext.resources.configuration)[0]
            val authorization: String =
                "Basic Y2tfZDQ4ZmU4ZjIxNDg4YmVlNWRlYmY4ZTYzZmY4YTA0NTg0NjMzZWRlYTpjc18yZDJiZTBmOTZhMzU0ZGI1MzQ2NzdiY2I5NTQ1OTBkYWVlZmM3MmQ2"

            val request: Request = original.newBuilder()
                .header("lang", currentLocale.language.toString())
                .header("authorization", authorization)
                .method(original.method(), original.body())
                .build()

            return chain.proceed(request)
        }catch (e: Exception){
            throw ApiException(Constant.API_ERROR)
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        var result = false
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }

}