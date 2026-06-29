package com.kira.kmpbase.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ContactDto(
    val id: Int,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null,
)
