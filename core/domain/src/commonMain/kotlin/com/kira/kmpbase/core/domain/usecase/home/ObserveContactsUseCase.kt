package com.kira.kmpbase.core.domain.usecase.home

import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.Contact
import com.kira.kmpbase.core.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class ObserveContactsUseCase(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(): Flow<AppResult<List<Contact>>> = homeRepository.observeContacts()
}
