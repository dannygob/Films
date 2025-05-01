package com.example.films.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    //https://www.omdbapi.com/?i=tt2380307&apikey=6ea7eac4
    // key 6ea7eac4
    @GET("?apikey=6ea7eac4")
    suspend fun findMoviesById(@Query("i") id: String): Movie

    @GET("?apikey=6ea7eac4")
    suspend fun searchMoviesByTitle(@Query("s") title: String): MoviesResponse

    //https://www.omdbapi.com/?s=coco&apikey=6ea7eac4
}