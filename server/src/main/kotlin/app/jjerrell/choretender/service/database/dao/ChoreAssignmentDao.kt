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
package app.jjerrell.choretender.service.database.dao

import androidx.room.*
import app.jjerrell.choretender.service.database.entity.ChoreAssignmentEntity
import app.jjerrell.choretender.service.database.entity.ChoreAssignmentWithChore
import app.jjerrell.choretender.service.database.entity.FamilyMemberWithAssignments

@Dao
interface ChoreAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: ChoreAssignmentEntity): Long

    @Update suspend fun updateAssignment(assignment: ChoreAssignmentEntity): Int

    @Delete suspend fun removeAssignment(assignment: ChoreAssignmentEntity): Int

    @Transaction
    @Query("SELECT * FROM chore_assignment WHERE assignmentId = :assignmentId")
    suspend fun getAssignmentById(assignmentId: Long): ChoreAssignmentWithChore

    @Transaction
    @Query("SELECT * FROM chore_assignment WHERE choreId = :choreId")
    suspend fun getExistingAssignment(choreId: Long): ChoreAssignmentWithChore

    @Transaction
    @Query("SELECT * FROM family_member WHERE memberId = :assigneeId")
    suspend fun getFamilyMemberWithAssignments(assigneeId: Long): FamilyMemberWithAssignments
}
