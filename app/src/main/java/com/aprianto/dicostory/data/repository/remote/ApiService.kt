package com.aprianto.dicostory.data.repository.remote

import com.aprianto.dicostory.data.model.Login
import com.aprianto.dicostory.data.model.Register
import com.aprianto.dicostory.data.model.StoryList
import com.aprianto.dicostory.data.model.StoryUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun doLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Login>

    @POST("register")
    @FormUrlEncoded
    fun doRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Register>

    @GET("stories")
    fun getStoryList(
        @Header("Authorization") token:String,
        @Query("size") size:Int
    ): Call<StoryList>

    @Multipart
    @POST("stories")
    fun doUploadImage(
        @Header("Authorization") token:String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<StoryUpload>
}