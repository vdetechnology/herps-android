package herbs.n.more.data.network

import herbs.n.more.BuildConfig
import herbs.n.more.data.network.responses.AuthResponse
import herbs.n.more.data.network.responses.BannerResponse
import herbs.n.more.data.network.responses.DetailProductResponse
import herbs.n.more.data.network.responses.GetBestSellingResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MyApi {

    @FormUrlEncoded
    @POST("users/login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("users/register")
    suspend fun userSignup(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<AuthResponse>

    @GET("product/bestseller/")
    suspend fun getBestSelling() : Response<GetBestSellingResponse>

    @GET("product/popular/")
    suspend fun getPopular(
        @Query("pageindex") pageindex : Int,
        @Query("pagesize") pagesize : Int
    ) : Response<GetBestSellingResponse>

    @GET("home/banners")
    suspend fun getBanners() : Response<BannerResponse>

    @GET("home/campaigns")
    suspend fun getCampaigns() : Response<BannerResponse>

    @GET("product/detail/")
    suspend fun getDetailProduct(
        @Query("productid") productid: String
    ): Response<DetailProductResponse>

    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi{

            val base_url = BuildConfig.API_URL

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}