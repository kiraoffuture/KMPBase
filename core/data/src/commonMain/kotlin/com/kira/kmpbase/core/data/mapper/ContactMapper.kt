package com.kira.kmpbase.core.data.mapper

import com.kira.kmpbase.core.domain.model.Contact
import com.kira.kmpbase.core.model.ContactDto

fun ContactDto.toDomain(): Contact = Contact(
    id = id,
    name = name,
    phone = phone,
    avatarUrl = avatarUrl,
)

fun List<ContactDto>.toDomain(): List<Contact> = map { it.toDomain() }
