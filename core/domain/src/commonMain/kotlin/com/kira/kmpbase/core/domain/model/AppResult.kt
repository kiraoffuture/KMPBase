package com.kira.kmpbase.core.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class AppError {
    data class Network(val message: String, val cause: Throwable? = null) : AppError()
    data class Database(val message: String, val cause: Throwable? = null) : AppError()
    data class Validation(val code: ValidationErrorCode) : AppError()
    data class Unknown(val message: String, val cause: Throwable? = null) : AppError()
}

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()
    data object Loading : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(error)
    return this
}

fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

suspend fun <T> safeCall(block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(AppError.Unknown(e.message ?: "Unknown error", e))
    }
}

fun <T> Flow<T>.asAppResult(): Flow<AppResult<T>> = this
    .map<T, AppResult<T>> { AppResult.Success(it) }
    .onStart { emit(AppResult.Loading) }
    .catch { emit(AppResult.Error(AppError.Unknown(it.message ?: "Flow error", it))) }
