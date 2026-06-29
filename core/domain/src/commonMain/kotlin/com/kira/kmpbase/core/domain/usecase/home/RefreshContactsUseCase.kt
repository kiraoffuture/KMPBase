package com.kira.kmpbase.core.domain.usecase.home

import com.kira.kmpbase.core.domain.model.AppResult
import com.kira.kmpbase.core.domain.model.Contact
import com.kira.kmpbase.core.domain.repository.HomeRepository

class RefreshContactsUseCase(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): AppResult<List<Contact>> = homeRepository.refreshContacts()
}
