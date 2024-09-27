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
import app.jjerrell.choretender.service.database.entity.ChoreEntity
import app.jjerrell.choretender.service.domain.model.chore.*
import io.ktor.util.logging.*

internal class ChoreRepository(private val db: ChoreServiceDatabase, private val logger: Logger) :
    IChoreServiceChoreRepository {

    override suspend fun getChoreDetail(familyId: Long, choreId: Long): ChoreDetailRead {
        return db.familyDao().getChore(familyId, choreId)
            .convertToDetail()
    }

    override suspend fun getFamilyChoreDetails(familyId: Long): List<ChoreDetailRead> {
        return db.familyDao().getFamilyWithChores(familyId).chores.map { it.convertToDetail() }
    }

    override suspend fun createChore(familyId: Long, detail: ChoreDetailCreate): ChoreDetailRead {
        val createdChoreId = db.familyDao().insertChore(detail.convertToEntity(familyId))
        return db.familyDao().getChore(familyId = familyId, choreId = createdChoreId)
            .convertToDetail()
    }

    override suspend fun updateChore(familyId: Long, detail: ChoreDetailUpdate): ChoreDetailRead {
        // Mutate the Chore in memory
        val existingChoreEntityUpdated = db.familyDao().getChore(familyId = familyId, choreId = detail.id)
            .updateWith(detail)

        // Update the Chore in the database
        db.familyDao().updateChore(existingChoreEntityUpdated)

        // Return the updated Chore
        return db.familyDao().getChore(familyId = familyId, choreId = existingChoreEntityUpdated.choreId)
            .convertToDetail()
    }
}

private fun ChoreEntity.convertToDetail() = ChoreDetailRead(
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

private fun ChoreDetailCreate.convertToEntity(familyId: Long) = ChoreEntity(
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

private fun ChoreEntity.updateWith(update: ChoreDetailUpdate) = copy(
    name = update.name ?: name,
    recurrence = update.recurrence?.name ?: recurrence,
    endDate = update.endDate ?: endDate,
    status = update.status?.name ?: status,
    updatedDate = update.updatedDate,
    updatedBy = update.updatedBy
)
