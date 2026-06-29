package com.kira.kmpbase.core.domain.model

data class Contact(
    val id: Int,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null,
)
