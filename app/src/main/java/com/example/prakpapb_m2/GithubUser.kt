package com.example.prakpapb_m2

data class GithubUser(
    val login: String,
    val name: String?,
    val avatar_url: String,
    val followers: Int,
    val following: Int
)