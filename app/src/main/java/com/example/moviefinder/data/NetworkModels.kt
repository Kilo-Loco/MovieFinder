package com.example.moviefinder.data

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("overview")
    val overview: String
) {
    val fullPostPath: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

data class TopRatedMoviesResponse(
    @SerializedName("results")
    val movies: List<Movie>
)

data class VideoStreamPlatform(
    @SerializedName("provider_name")
    val name: String
)

data class VideoProviderRegion(
    @SerializedName("flatrate")
    val streamingPlatforms: List<VideoStreamPlatform>
)

data class  VideoProviderResponseResults(
    @SerializedName("US")
    val us: VideoProviderRegion?
)

data class VideoProviderResponse(
    @SerializedName("results")
    val results: VideoProviderResponseResults
)