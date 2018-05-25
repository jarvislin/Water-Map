package com.jarvislin.watermap.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class SearchResult(
        @SerializedName("type")
        val type: String,
        @SerializedName("features")
        val features: List<Feature>
)

data class Feature(
        @SerializedName("type")
        val type: String,
        @SerializedName("geometry")
        val geometry: Geometry,
        @SerializedName("properties")
        val property: Property
)

data class Geometry(
        @SerializedName("type")
        val type: String,
        @SerializedName("coordinates")
        val coordinates: List<Long>
)

data class Property(
        @SerializedName("id")
        val id: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("comment_url")
        val commentUrl: String,
        @SerializedName("close_url")
        val closeUrl: String,
        @SerializedName("date_created")
        val dateCreated: Date,
        @SerializedName("status")
        val status: String,
        @SerializedName("comments")
        val comments: List<Comment>
)

data class Comment(
        @SerializedName("date")
        val date: Date,
        @SerializedName("action")
        val action: String,
        @SerializedName("text")
        val text: String,
        @SerializedName("html")
        val html: String
)