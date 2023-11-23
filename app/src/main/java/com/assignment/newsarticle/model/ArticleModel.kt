package com.assignment.newsarticle.model

data class ArticleModel(
    val title: String,
    val source: Source,
    val author: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String,
    val publishedAt: String?,
    val  content: String?,
    var expanded: Boolean?
)

data class Source(
    val id: String,
    val name:String
)
