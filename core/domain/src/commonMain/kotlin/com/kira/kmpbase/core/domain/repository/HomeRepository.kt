package com.kira.kmpbase.core.domain.repository

import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeContacts(): Flow<AppResult<List<Contact>>>

    suspend fun refreshContacts(): AppResult<List<Contact>>
}
