package com.jarvislin.watermap.data.remote

import com.jarvislin.watermap.data.models.SearchResult
import io.reactivex.Single
import retrofit2.http.GET

interface Api {

    @GET("notes/search.json?q=飲水地圖")
    fun search(): Single<SearchResult>
}