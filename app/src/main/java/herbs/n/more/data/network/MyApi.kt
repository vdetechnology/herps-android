package herbs.n.more.data.network

import herbs.n.more.BuildConfig
import herbs.n.more.data.network.responses.*
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

    @FormUrlEncoded
    @POST("users/forgotpassword")
    suspend fun userForgotPassword(
        @Field("email") email: String
    ) : Response<AuthResponse>

    @GET("product/bestseller/")
    suspend fun getBestSelling() : Response<GetBestSellingResponse>

    @GET("product/bestseller/")
    suspend fun getBestSellingFull(
        @Query("pagesize") pagesize: String
    ) : Response<GetBestSellingResponse>


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

    @FormUrlEncoded
    @POST("users/forgotpassword")
    suspend fun getDiscountCode(
        @Field("email") email: String
    ) : Response<GetDiscountCodeResponse>

    @FormUrlEncoded
    @POST("users/LoginByZalo")
    suspend fun userLoginByZalo(
        @Field("zaloId") zaloId: String,
        @Field("username") username: String,
        @Field("picture") picture: String
    ): Response<AuthResponse>

    @GET("product/comments/")
    suspend fun getComments(
        @Query("productid") productid : String,
        @Query("pageindex") pageindex : Int,
        @Query("pagesize") pagesize : Int
    ) : Response<GetCommentsResponse>

    @FormUrlEncoded
    @POST("product/addcomment")
    suspend fun addComment(
        @Field("productid") productid: String,
        @Field("userid") userid: String,
        @Field("email") email: String,
        @Field("reviewer") reviewer: String,
        @Field("rating") rating: Int,
        @Field("comment") comment: String,
        @Field("agent") agent: String
    ): Response<AddCommentResponse>

    @GET("product/relatedproducts/")
    suspend fun getRelatedProduct(
        @Query("productid") productid : Int,
        @Query("pagesize") pagesize : Int
    ) : Response<GetBestSellingResponse>

    @GET("product/popularSearches/")
    suspend fun getPopularSearches(
        @Query("pageindex") pageindex : Int,
        @Query("pagesize") pagesize : Int
    ) : Response<PopularSearchesResponse>

    @GET("product/categories")
    suspend fun getCategories() : Response<CategoryResponse>

    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi{

            val baseUrl = BuildConfig.API_URL

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}