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
import app.jjerrell.choretender.service.database.entity.*

@Dao
interface FamilyDao {

    //region Family and Members
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: FamilyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(familyMember: FamilyMemberEntity): Long

    @Update suspend fun updateFamilyMember(familyMember: FamilyMemberEntity): Int

    @Delete suspend fun removeFamilyMember(familyMember: FamilyMemberEntity): Int

    // Query to get family with all its members
    @Transaction
    @Query("SELECT * FROM family WHERE familyId = :familyId")
    suspend fun getFamilyWithMembers(familyId: Long): FamilyWithMembers
    //endregion
    //region Family Chores
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChore(chore: ChoreEntity): Long

    @Update
    suspend fun updateChore(chore: ChoreEntity): Int

    @Transaction
    @Query("SELECT * FROM chore WHERE familyId = :familyId AND choreId = :choreId")
    suspend fun getChore(familyId: Long, choreId: Long): ChoreEntity

    @Transaction
    @Query("SELECT * FROM family WHERE familyId = :familyId")
    suspend fun getFamilyWithChores(familyId: Long): FamilyWithChores
    //endregion
}
