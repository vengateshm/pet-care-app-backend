package dev.vengateshm.models

import kotlinx.serialization.Serializable

@Serializable
data class AppService(
    val id: Int?=null,
    val name: String,
    val imgUrl:String
)
