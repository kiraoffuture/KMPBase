package com.kira.kmpbase.core.network

class HomeApiService(
    private val homeApi: HomeApi,
) {
    suspend fun getContacts() = homeApi.getContacts().data
        ?: throw IllegalStateException("Contact list response did not contain data")
}
