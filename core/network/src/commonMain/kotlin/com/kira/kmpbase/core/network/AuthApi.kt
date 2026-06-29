package com.kira.kmpbase.core.network

import com.kira.kmpbase.core.model.BaseResponse
import com.kira.kmpbase.core.model.LoginRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface AuthApi {
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<String>
}
