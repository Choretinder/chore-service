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
package app.jjerrell.choretender.service.domain.model.chore

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

enum class ChoreRecurrence {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

enum class ChoreCompletion {
    NONE,
    VERIFY,
    REDO,
    DONE
}

@Serializable
data class ChoreDetailCreate(
    val name: String,
    val recurrence: ChoreRecurrence = ChoreRecurrence.NONE,
    val createdDate: Long = Clock.System.now().epochSeconds,
    val createdBy: Long,
    val endDate: Instant? = null,
)

@Serializable
data class ChoreDetailRead(
    val id: Long,
    val name: String,
    val recurrence: ChoreRecurrence,
    val createdDate: Long,
    val createdBy: Long,
    val endDate: Instant?,
    val status: ChoreCompletion,
    val updatedDate: Long?,
    val updatedBy: Long?
)

@Serializable
data class ChoreDetailUpdate(
    val id: Long,
    val name: String,
    val recurrence: ChoreRecurrence,
    val endDate: Instant?,
    val status: ChoreCompletion,
    val updatedDate: Long = Clock.System.now().epochSeconds,
    val updatedBy: Long
)
