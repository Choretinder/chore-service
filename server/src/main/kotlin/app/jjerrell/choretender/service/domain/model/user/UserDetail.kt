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

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

enum class UserType {
    STANDARD,
    MANAGER
}

@Serializable
data class UserDetailCreate(
    val name: String,
    val type: UserType,
    val contactInfo: ContactInfo?,
    val createdBy: Long,
    val createdDate: Long = Clock.System.now().epochSeconds
)

@Serializable
data class UserDetailRead(
    val id: Long,
    val name: String,
    val type: UserType,
    val contactInfo: ContactInfo?,
    val createdBy: Long,
    val createdDate: Long,
    val updatedDate: Long?,
    val updatedBy: Long?
)

@Serializable
data class UserDetailUpdate(
    val id: Long,
    val name: String? = null,
    val type: UserType? = null,
    val contactInfo: ContactInfo? = null,
    val updatedDate: Long = Clock.System.now().epochSeconds,
    val updatedBy: Long
)
