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

import androidx.room.*

@Entity(
    tableName = "chore_assignment",
    foreignKeys =
        [
            ForeignKey(
                entity = FamilyMemberEntity::class,
                parentColumns = ["memberId"],
                childColumns = ["assigneeId"],
                onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                entity = ChoreEntity::class,
                parentColumns = ["choreId"],
                childColumns = ["choreId"],
                onDelete = ForeignKey.CASCADE
            )
        ],
    indices = [Index(value = ["assigneeId"]), Index(value = ["choreId"])]
)
data class ChoreAssignment(
    @PrimaryKey(autoGenerate = true) val assignmentId: Long = 0,
    val assigneeId: Long,
    val choreId: Long,
    val assignmentStatus: String,
    val assignmentDate: Long,
    val statusDate: Long = assignmentDate
)

data class ChoreAssignmentWithChore(
    @Embedded val assignment: ChoreAssignment,
    @Relation(parentColumn = "choreId", entityColumn = "choreId") val chore: ChoreEntity
)

data class FamilyMemberWithAssignments(
    @Embedded val member: FamilyMemberEntity,
    @Relation(
        entity = ChoreAssignment::class,
        parentColumn = "memberId",
        entityColumn = "assigneeId"
    )
    val assignmentsWithChore: List<ChoreAssignmentWithChore>
)
