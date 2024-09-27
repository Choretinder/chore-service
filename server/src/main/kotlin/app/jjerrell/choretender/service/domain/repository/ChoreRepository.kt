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
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailCreate
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailRead
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailUpdate
import io.ktor.util.logging.*

internal class ChoreRepository(private val db: ChoreServiceDatabase, private val logger: Logger) :
    IChoreServiceChoreRepository {

    override suspend fun getChoreDetail(familyId: Long, choreId: Long): ChoreDetailRead? {
        val family = db.familyDao().getFamilyWithMembers(familyId)

        return null
    }

    override suspend fun getFamilyChoreDetails(familyId: Long): List<ChoreDetailRead> {
        val family = db.familyDao().getFamilyWithMembers(familyId)

        return listOf()
    }

    override suspend fun createChore(familyId: Long, detail: ChoreDetailCreate): ChoreDetailRead? {
        val family = db.familyDao().getFamilyWithMembers(familyId)

        return null
    }

    override suspend fun updateChore(familyId: Long, detail: ChoreDetailUpdate): ChoreDetailRead? {
        val family = db.familyDao().getFamilyWithMembers(familyId)

        return null
    }
}
