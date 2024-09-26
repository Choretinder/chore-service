/*
 * Choretinder
 * Copyright (C) 2024  Jacob Jerrell (@jjerrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package app.jjerrell.choretender.service.domain.repository

import app.jjerrell.choretender.service.database.ChoreServiceDatabase
import app.jjerrell.choretender.service.database.entity.UserEntity
import app.jjerrell.choretender.service.database.entity.UserEntityContact
import app.jjerrell.choretender.service.domain.IChoreServiceUserRepository
import app.jjerrell.choretender.service.domain.model.user.ContactInfo
import app.jjerrell.choretender.service.domain.model.user.ContactType
import app.jjerrell.choretender.service.domain.model.user.UserDetailCreate
import app.jjerrell.choretender.service.domain.model.user.UserDetailRead
import app.jjerrell.choretender.service.domain.model.user.UserDetailUpdate
import app.jjerrell.choretender.service.domain.model.user.UserType
import io.ktor.util.logging.*
import kotlinx.datetime.Clock

internal class UserRepository(private val db: ChoreServiceDatabase, private val logger: Logger) :
    IChoreServiceUserRepository {
    override suspend fun getUserDetail(id: Long): UserDetailRead {
        return db.userDao().getUserById(id).toReadUser()
    }

    override suspend fun createUser(detail: UserDetailCreate): UserDetailRead {
        val newUserId = db.userDao().insertUser(detail.toEntityUser())
        return getUserDetail(newUserId)
    }

    override suspend fun updateUser(detail: UserDetailUpdate): UserDetailRead {
        val targetUserUpdate =
            db.userDao()
                .getUserById(detail.id)
                .copy(
                    name = detail.name,
                    userType = detail.type.name,
                    updatedDateSeconds = detail.updatedDate ?: Clock.System.now().epochSeconds,
                    updatedBy = detail.updatedBy
                )
                .let {
                    if (detail.contactInfo != null) {
                        it.copy(contact = detail.contactInfo.toContactEntity())
                    } else {
                        it
                    }
                }
        // operation
        db.userDao().updateUser(user = targetUserUpdate)
        // return value
        return db.userDao().getUserById(targetUserUpdate.userId).toReadUser()
    }
}

// region Read User
private fun UserEntity.toReadUser(): UserDetailRead =
    UserDetailRead(
        id = userId,
        name = name,
        type = UserType.valueOf(userType),
        contactInfo = contact?.toContactDetail(),
        createdBy = createdBy,
        createdDate = createdDateSeconds,
        updatedBy = updatedBy,
        updatedDate = updatedDateSeconds
    )
// endregion

// region Create User
private fun UserDetailCreate.toEntityUser(): UserEntity =
    UserEntity(
        name = name,
        userType = type.name,
        contact = contactInfo?.toContactEntity(),
        createdBy = createdBy,
        createdDateSeconds = createdDate,
        updatedDateSeconds = null,
        updatedBy = null
    )
// endregion

// region Contact Info
internal fun UserEntityContact.toContactDetail() =
    ContactInfo(
        resource = resource,
        type = ContactType.valueOf(contactType),
        isVerified = isVerified
    )

private fun ContactInfo.toContactEntity() =
    UserEntityContact(resource = resource, contactType = type.name, isVerified = isVerified)
// endregion
