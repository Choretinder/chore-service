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
package app.jjerrell.choretender.service.domain.model.user

import app.jjerrell.choretender.service.domain.model.ContactInfo
import app.jjerrell.choretender.service.domain.model.common.ICreatable
import app.jjerrell.choretender.service.domain.model.common.IIdentifiable
import app.jjerrell.choretender.service.domain.model.common.IUpdateable
import kotlinx.serialization.Serializable

enum class UserType {
    STANDARD,
    MANAGER
}

sealed interface UserDetail {
    val name: String
    val type: UserType
    val contactInfo: ContactInfo?
}

@Serializable
data class UserDetailCreate(
    override val name: String,
    override val type: UserType,
    override val contactInfo: ContactInfo?,
    override val createdBy: Long,
    override val createdDate: Long
) : UserDetail, ICreatable

@Serializable
data class UserDetailRead(
    override val id: Long,
    override val name: String,
    override val type: UserType,
    override val contactInfo: ContactInfo?,
    override val createdBy: Long,
    override val createdDate: Long,
    override val updatedDate: Long?,
    override val updatedBy: Long?
) : UserDetail, IIdentifiable, ICreatable, IUpdateable

@Serializable
data class UserDetailUpdate(
    override val id: Long,
    override val name: String,
    override val type: UserType,
    override val contactInfo: ContactInfo?,
    override val updatedDate: Long?,
    override val updatedBy: Long
) : UserDetail, IIdentifiable, IUpdateable

@Serializable
data class FamilyMemberDetail(
    override val id: Long,
    override val name: String,
    override val type: UserType,
    override val contactInfo: ContactInfo?,
    val isVerified: Boolean
) : UserDetail, IIdentifiable
