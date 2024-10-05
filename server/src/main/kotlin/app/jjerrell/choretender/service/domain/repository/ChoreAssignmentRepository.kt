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

import app.jjerrell.choretender.service.database.dao.ChoreAssignmentDao
import app.jjerrell.choretender.service.database.entity.ChoreAssignmentEntity
import app.jjerrell.choretender.service.database.entity.ChoreAssignmentWithChore
import app.jjerrell.choretender.service.database.service.ChoreServiceDatabase
import app.jjerrell.choretender.service.domain.model.chore.ChoreCompletion
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailRead
import io.ktor.util.logging.*
import kotlinx.datetime.Clock

interface IChoreAssignmentRepository {
    suspend fun assignChore(id: Long, assignee: Long): ChoreAssignmentDetail

    suspend fun updateAssignment(assignmentId: Long, status: ChoreCompletion): ChoreAssignmentDetail

    suspend fun getChoreAssignments(assigneeId: Long): List<ChoreDetailRead>
}

internal class ChoreAssignmentRepository(
    private val db: ChoreServiceDatabase,
    private val logger: Logger
) : IChoreAssignmentRepository {
    override suspend fun assignChore(id: Long, assignee: Long): ChoreAssignmentDetail {
        val dao = db.choreAssignmentDao()
        return when (val assignment = existingAssignment(dao, id)) {
            null -> {
                val assignmentId =
                    dao.insertAssignment(
                        assignment =
                            ChoreAssignmentEntity(
                                assigneeId = assignee,
                                choreId = id,
                                assignmentStatus = ChoreCompletion.NONE.name,
                                assignmentDate = Clock.System.now().toEpochMilliseconds()
                            )
                    )

                dao.getAssignmentById(assignmentId).convertToDetail()
            }
            else -> assignment.convertToDetail()
        }
    }

    override suspend fun updateAssignment(
        assignmentId: Long,
        status: ChoreCompletion
    ): ChoreAssignmentDetail {
        val dao = db.choreAssignmentDao()
        val assignmentDetail = dao.getAssignmentById(assignmentId).assignment
        val updatedAssignmentEntity =
            assignmentDetail.copy(
                assignmentStatus = status.name,
                statusDate = Clock.System.now().toEpochMilliseconds()
            )
        // Update the assignment
        dao.updateAssignment(updatedAssignmentEntity)

        // Get and return the updated assignment detail
        return dao.getAssignmentById(updatedAssignmentEntity.assignmentId).convertToDetail()
    }

    override suspend fun getChoreAssignments(assigneeId: Long): List<ChoreDetailRead> {
        return db.choreAssignmentDao()
            .getFamilyMemberWithAssignments(assigneeId)
            .assignmentsWithChore
            .map { it.chore.convertToDetail() }
    }

    private suspend fun existingAssignment(
        dao: ChoreAssignmentDao,
        choreId: Long
    ): ChoreAssignmentWithChore? {
        return try {
            dao.getExistingAssignment(choreId)
        } catch (e: Throwable) {
            null
        }
    }
}

data class ChoreAssignmentDetail(
    val assignmentId: Long,
    val assignmentStatus: ChoreCompletion,
    val assignmentDate: Long,
    val statusDate: Long,
    val assignment: ChoreDetailRead
)

private fun ChoreAssignmentWithChore.convertToDetail() =
    ChoreAssignmentDetail(
        assignmentId = assignment.assignmentId,
        assignmentStatus = ChoreCompletion.valueOf(assignment.assignmentStatus),
        assignmentDate = assignment.assignmentDate,
        statusDate = assignment.statusDate,
        assignment = chore.convertToDetail()
    )
