package com.kira.kmpbase.core.network

import com.kira.kmpbase.core.model.BaseResponse
import com.kira.kmpbase.core.model.ContactDto
import de.jensklingenberg.ktorfit.http.GET

interface HomeApi {
    @GET("contact/list")
    suspend fun getContacts(): BaseResponse<List<ContactDto>>
}
