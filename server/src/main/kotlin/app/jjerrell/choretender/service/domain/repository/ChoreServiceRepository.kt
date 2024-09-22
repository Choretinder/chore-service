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
package app.jjerrell.choretender.service.domain

import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailCreate
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailRead
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailUpdate
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailCreate
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailRead
import app.jjerrell.choretender.service.domain.model.user.FamilyMemberDetail
import app.jjerrell.choretender.service.domain.model.user.UserDetailCreate
import app.jjerrell.choretender.service.domain.model.user.UserDetailRead
import app.jjerrell.choretender.service.domain.model.user.UserDetailUpdate

interface IChoreServiceFamilyRepository {
    suspend fun getFamilyDetail(id: Long): FamilyDetailRead?

    suspend fun createFamily(detail: FamilyDetailCreate)

    suspend fun inviteFamilyMember(familyId: Long, detail: FamilyMemberDetail)

    suspend fun leaveFamilyGroup(familyId: Long, userId: Long)
}

interface IChoreServiceUserRepository {
    suspend fun getUserDetail(id: Long): UserDetailRead?

    suspend fun createUser(detail: UserDetailCreate): UserDetailRead?

    suspend fun updateUser(detail: UserDetailUpdate): UserDetailRead?
}

interface IChoreServiceChoreRepository {
    suspend fun getChoreDetail(id: Long): ChoreDetailRead

    suspend fun createChore(detail: ChoreDetailCreate)

    suspend fun updateChore(detail: ChoreDetailUpdate)
}
