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

import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailCreate
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailRead
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailUpdate
import app.jjerrell.choretender.service.domain.model.family.*
import app.jjerrell.choretender.service.domain.model.user.UserDetailCreate
import app.jjerrell.choretender.service.domain.model.user.UserDetailRead
import app.jjerrell.choretender.service.domain.model.user.UserDetailUpdate

interface IChoreServiceFamilyRepository {
    suspend fun getFamilyDetail(id: Long): FamilyDetailRead

    suspend fun createFamily(detail: FamilyDetailCreate): FamilyDetailRead

    suspend fun inviteFamilyMember(familyId: Long, detail: FamilyDetailInvite): FamilyDetailRead

    suspend fun verifyFamilyMember(familyId: Long, detail: FamilyMemberVerify): FamilyDetailRead

    suspend fun changeMemberRole(familyId: Long, detail: FamilyMemberChangeRole): FamilyDetailRead

    suspend fun leaveFamilyGroup(familyId: Long, detail: FamilyMemberLeave): FamilyDetailRead
}

interface IChoreServiceUserRepository {
    suspend fun getUserDetail(id: Long): UserDetailRead?

    suspend fun createUser(detail: UserDetailCreate): UserDetailRead?

    suspend fun updateUser(detail: UserDetailUpdate): UserDetailRead?
}

interface IChoreServiceChoreRepository {
    suspend fun getChoreDetail(familyId: Long, choreId: Long): ChoreDetailRead

    suspend fun getFamilyChoreDetails(familyId: Long): List<ChoreDetailRead>

    suspend fun createChore(familyId: Long, detail: ChoreDetailCreate): ChoreDetailRead

    suspend fun updateChore(familyId: Long, detail: ChoreDetailUpdate): ChoreDetailRead
}
