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

import app.jjerrell.choretender.service.database.entity.ChoreEntity
import app.jjerrell.choretender.service.database.service.ChoreServiceDatabase
import app.jjerrell.choretender.service.domain.model.chore.*
import io.ktor.util.logging.*

interface IChoreRepository {
    suspend fun getChoreDetail(familyId: Long, choreId: Long): ChoreDetailRead

    suspend fun getFamilyChoreDetails(familyId: Long): List<ChoreDetailRead>

    suspend fun createChore(familyId: Long, detail: ChoreDetailCreate): ChoreDetailRead

    suspend fun updateChore(familyId: Long, detail: ChoreDetailUpdate): ChoreDetailRead
}

internal class ChoreRepository(private val db: ChoreServiceDatabase, private val logger: Logger) :
    IChoreRepository {

    override suspend fun getChoreDetail(familyId: Long, choreId: Long): ChoreDetailRead {
        return db.familyDao().getChore(familyId, choreId).convertToDetail()
    }

    override suspend fun getFamilyChoreDetails(familyId: Long): List<ChoreDetailRead> {
        return db.familyDao().getFamilyWithChores(familyId).chores.map { it.convertToDetail() }
    }

    override suspend fun createChore(familyId: Long, detail: ChoreDetailCreate): ChoreDetailRead {
        val createdChoreId = db.familyDao().insertChore(detail.convertToEntity(familyId))
        return getChoreDetail(familyId = familyId, choreId = createdChoreId)
    }

    override suspend fun updateChore(familyId: Long, detail: ChoreDetailUpdate): ChoreDetailRead {
        // Mutate the Chore in memory
        val existingChoreUpdated =
            getChoreDetail(familyId = familyId, choreId = detail.id)
                .let {
                    it.copy(
                        name = detail.name ?: it.name,
                        recurrence = detail.recurrence ?: it.recurrence,
                        endDate = detail.endDate ?: it.endDate,
                        status = detail.status ?: it.status,
                        updatedDate = detail.updatedDate,
                        updatedBy = detail.updatedBy
                    )
                }
                .convertToEntity(familyId)

        // Update the Chore in the database
        db.familyDao().updateChore(existingChoreUpdated)

        // Return the updated Chore
        return getChoreDetail(familyId = familyId, choreId = existingChoreUpdated.choreId)
    }
}

internal fun ChoreEntity.convertToDetail() =
    ChoreDetailRead(
        id = choreId,
        name = name,
        recurrence = ChoreRecurrence.valueOf(recurrence),
        createdDate = createdDate,
        createdBy = createdBy,
        endDate = endDate,
        status = ChoreCompletion.valueOf(status),
        updatedBy = updatedBy,
        updatedDate = updatedDate
    )

private fun ChoreDetailRead.convertToEntity(familyId: Long) =
    ChoreEntity(
        choreId = id,
        familyId = familyId,
        name = name,
        recurrence = recurrence.name,
        createdDate = createdDate,
        createdBy = createdBy,
        endDate = endDate,
        status = status.name,
        updatedBy = updatedBy,
        updatedDate = updatedDate
    )

private fun ChoreDetailCreate.convertToEntity(familyId: Long) =
    ChoreEntity(
        familyId = familyId,
        name = name,
        recurrence = recurrence.name,
        status = ChoreCompletion.NONE.name,
        endDate = endDate,
        createdBy = createdBy,
        createdDate = createdDate,
        updatedBy = null,
        updatedDate = null
    )
