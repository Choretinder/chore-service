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
    tableName = "chore",
    foreignKeys =
        [
            ForeignKey(
                entity = FamilyEntity::class,
                parentColumns = ["familyId"],
                childColumns = ["familyId"],
                onDelete = ForeignKey.CASCADE
            )
        ],
    indices = [Index(value = ["familyId"])]
)
data class ChoreEntity(
    @PrimaryKey(autoGenerate = true) val choreId: Long = 0,
    val familyId: Long,
    val name: String,
    val recurrence: String,
    val status: String,
    val endDate: Long?,
    val createdBy: Long,
    val createdDate: Long,
    val updatedBy: Long?,
    val updatedDate: Long?
)

data class FamilyWithChores(
    @Embedded val family: FamilyEntity,
    @Relation(parentColumn = "familyId", entityColumn = "familyId") val chores: List<ChoreEntity>
)
