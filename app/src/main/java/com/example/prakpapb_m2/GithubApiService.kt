package com.example.prakpapb_m2

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {
    @GET("users/{username}")
    suspend fun getUserProfile(@Path("username") username: String): GithubUser // Corrected to accept String
}