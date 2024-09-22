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

import app.jjerrell.choretender.service.domain.model.common.ICreatable
import app.jjerrell.choretender.service.domain.model.common.IIdentifiable
import app.jjerrell.choretender.service.domain.model.common.IUpdateable
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

sealed interface ChoreDetail {
    val name: String
    val recurrence: ChoreRecurrence
    val endDate: Instant?
}

@Serializable
data class ChoreDetailCreate(
    override val name: String,
    override val recurrence: ChoreRecurrence = ChoreRecurrence.NONE,
    override val createdDate: Long = Clock.System.now().epochSeconds,
    override val createdBy: Long,
    override val endDate: Instant? = null,
) : ChoreDetail, ICreatable

@Serializable
data class ChoreDetailRead(
    override val id: Long,
    override val name: String,
    override val recurrence: ChoreRecurrence,
    override val createdDate: Long,
    override val createdBy: Long,
    override val endDate: Instant?,
    val status: ChoreCompletion,
    override val updatedDate: Long?,
    override val updatedBy: Long?
) : ChoreDetail, IIdentifiable, ICreatable, IUpdateable

@Serializable
data class ChoreDetailUpdate(
    override val id: Long,
    override val name: String,
    override val recurrence: ChoreRecurrence,
    override val endDate: Instant?,
    val status: ChoreCompletion,
    override val updatedDate: Long = Clock.System.now().epochSeconds,
    override val updatedBy: Long
) : ChoreDetail, IIdentifiable, IUpdateable
