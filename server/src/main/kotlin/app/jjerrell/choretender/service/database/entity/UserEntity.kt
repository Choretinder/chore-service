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
package app.jjerrell.choretender.service.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val name: String,
    val userType: String,
    @Embedded val contact: UserEntityContact?,
    val createdBy: Long,
    val createdDateSeconds: Long,
    val updatedDateSeconds: Long?,
    val updatedBy: Long?
)

data class UserEntityContact(
    val resource: String,
    val contactType: String,
    val isVerified: Boolean
)
