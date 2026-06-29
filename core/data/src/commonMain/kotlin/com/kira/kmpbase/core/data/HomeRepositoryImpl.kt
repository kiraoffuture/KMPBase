package com.kira.kmpbase.core.data

import com.kira.kmpbase.core.common.DispatcherProvider
import com.kira.kmpbase.core.data.mapper.toDomain
import com.kira.kmpbase.core.data.util.safeApiCall
import com.kira.kmpbase.core.data.util.safeDatabaseCall
import com.kira.kmpbase.core.database.CacheDao
import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.Contact
import com.kira.kmpbase.core.domain.repository.HomeRepository
import com.kira.kmpbase.core.model.ContactDto
import com.kira.kmpbase.core.network.HomeApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class HomeRepositoryImpl(
    private val apiService: HomeApiService,
    private val cacheDao: CacheDao,
    private val dispatchers: DispatcherProvider,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : HomeRepository {

    override fun observeContacts(): Flow<AppResult<List<Contact>>> = flow {
        emit(AppResult.Loading)

        var cachedContacts: List<Contact>? = null
        var cacheError: AppResult.Error? = null

        when (val cacheResult = readCachedContacts()) {
            is AppResult.Success -> {
                cachedContacts = cacheResult.data
                emit(cacheResult)
            }
            is AppResult.Error -> cacheError = cacheResult
            null, AppResult.Loading -> Unit
        }

        when (val result = refreshContacts()) {
            is AppResult.Success -> emit(result)
            is AppResult.Error -> {
                if (cachedContacts == null) {
                    emit(cacheError ?: result)
                }
            }
            AppResult.Loading -> Unit
        }
    }.catch { throwable ->
        emit(AppResult.Error(AppError.Unknown(throwable.message ?: "Flow error", throwable)))
    }.flowOn(dispatchers.io)

    override suspend fun refreshContacts(): AppResult<List<Contact>> = withContext(dispatchers.io) {
        when (val apiResult = safeApiCall { apiService.getContacts() }) {
            is AppResult.Success -> safeDatabaseCall {
                val contacts = apiResult.data.toDomain()
                cacheDao.put(
                    key = CACHE_KEY_CONTACTS,
                    value = json.encodeToString(ListSerializer(ContactDto.serializer()), apiResult.data),
                    updatedAt = Clock.System.now().toEpochMilliseconds(),
                )
                contacts
            }
            is AppResult.Error -> apiResult
            AppResult.Loading -> AppResult.Error(AppError.Unknown("Unexpected loading state"))
        }
    }

    private suspend fun readCachedContacts(): AppResult<List<Contact>>? {
        val cachedValue = when (val cached = safeDatabaseCall { cacheDao.get(CACHE_KEY_CONTACTS) }) {
            is AppResult.Success -> cached.data ?: return null
            is AppResult.Error -> return cached
            AppResult.Loading -> return null
        }

        return safeDatabaseCall {
            json.decodeFromString(ListSerializer(ContactDto.serializer()), cachedValue).toDomain()
        }
    }

    companion object {
        private const val CACHE_KEY_CONTACTS = "contacts"
    }
}
