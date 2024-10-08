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

import app.jjerrell.choretender.service.database.entity.FamilyEntity
import app.jjerrell.choretender.service.database.entity.FamilyMemberEntity
import app.jjerrell.choretender.service.database.entity.FamilyWithMembers
import app.jjerrell.choretender.service.database.service.ChoreServiceDatabase
import app.jjerrell.choretender.service.domain.model.family.*
import app.jjerrell.choretender.service.domain.model.user.UserType
import io.ktor.server.plugins.*
import io.ktor.util.logging.*
import kotlinx.datetime.Clock

interface IFamilyRepository {
    suspend fun getFamilyDetail(id: Long): FamilyDetailRead

    suspend fun createFamily(detail: FamilyDetailCreate): FamilyDetailRead

    suspend fun inviteFamilyMember(familyId: Long, detail: FamilyDetailInvite): FamilyDetailRead

    suspend fun verifyFamilyMember(familyId: Long, detail: FamilyMemberVerify): FamilyDetailRead

    suspend fun changeMemberRole(familyId: Long, detail: FamilyMemberChangeRole): FamilyDetailRead

    suspend fun leaveFamilyGroup(familyId: Long, detail: FamilyMemberLeave): FamilyDetailRead
}

internal class FamilyRepository(private val db: ChoreServiceDatabase, private val logger: Logger) :
    IFamilyRepository {
    override suspend fun getFamilyDetail(id: Long): FamilyDetailRead {
        return db.familyDao().getFamilyWithMembers(familyId = id).toFamilyDetail()
    }

    override suspend fun createFamily(detail: FamilyDetailCreate): FamilyDetailRead {
        val user = db.userDao().getUserById(detail.createdBy)

        // Create the family
        val familyEntity =
            FamilyEntity(
                familyName = detail.name,
                createdBy = detail.createdBy,
                createdDate = detail.createdDate,
                updatedBy = null,
                updatedDate = null
            )
        val familyId = db.familyDao().insertFamily(familyEntity)

        // Insert creator as the original member
        db.familyDao()
            .insertFamilyMember(
                FamilyMemberEntity(
                    familyId = familyId,
                    user = user,
                    role = UserType.MANAGER.name,
                    isConfirmed = true,
                    invitedBy = 0,
                    invitedDate = Clock.System.now().toEpochMilliseconds()
                )
            )

        // Insert any invitees
        detail.invitees?.forEach {
            inviteFamilyMember(
                familyId,
                FamilyDetailInvite(inviteeId = it, invitedBy = user.userId)
            )
        }

        // Get the family detail
        return getFamilyDetail(familyId)
    }

    override suspend fun inviteFamilyMember(
        familyId: Long,
        detail: FamilyDetailInvite
    ): FamilyDetailRead {
        val foundInvitee = db.userDao().getUserById(detail.inviteeId)
        val isNewMember =
            db.familyDao().getFamilyWithMembers(familyId).members.none {
                it.user.userId == detail.inviteeId
            }
        // Insert the invitee if located and new
        if (isNewMember) {
            db.familyDao()
                .insertFamilyMember(
                    FamilyMemberEntity(
                        familyId = familyId,
                        user = foundInvitee,
                        role = UserType.STANDARD.name,
                        isConfirmed = false,
                        invitedBy = detail.invitedBy,
                        invitedDate = Clock.System.now().toEpochMilliseconds()
                    )
                )
        }
        // Get the family detail
        return getFamilyDetail(familyId)
    }

    override suspend fun verifyFamilyMember(
        familyId: Long,
        detail: FamilyMemberVerify
    ): FamilyDetailRead {
        val members = db.familyDao().getFamilyWithMembers(familyId).members
        val invitee =
            members
                .filterNot { it.isConfirmed }
                .firstOrNull { it.memberId == detail.memberId }
                ?.copy(isConfirmed = true)
        return if (invitee == null) {
            throw NotFoundException("Member not found")
        } else {
            db.familyDao().updateFamilyMember(invitee)
            getFamilyDetail(familyId)
        }
    }

    override suspend fun changeMemberRole(
        familyId: Long,
        detail: FamilyMemberChangeRole
    ): FamilyDetailRead {
        val members = db.familyDao().getFamilyWithMembers(familyId).members
        val targetMember =
            members.filter { it.isConfirmed }.firstOrNull { it.memberId == detail.memberId }

        return if (targetMember == null) {
            throw NotFoundException("Verified member not found")
        } else if (
            targetMember.role == UserType.MANAGER.name &&
                members.count { it.role == UserType.MANAGER.name } == 1
        ) {
            throw UnsupportedOperationException(
                "Family contains a single Manager. Please promote another member first."
            )
        } else {
            db.familyDao().updateFamilyMember(targetMember.copy(role = detail.role.name))
            getFamilyDetail(familyId)
        }
    }

    override suspend fun leaveFamilyGroup(
        familyId: Long,
        detail: FamilyMemberLeave
    ): FamilyDetailRead {
        val members = db.familyDao().getFamilyWithMembers(familyId).members
        val foundMember = members.firstOrNull { it.memberId == detail.memberId }
        return when (foundMember?.let { UserType.valueOf(it.role) }) {
            UserType.STANDARD -> removeMember(foundMember)
            UserType.MANAGER ->
                if (members.count { it.role == UserType.MANAGER.name } > 1) {
                    removeMember(foundMember)
                } else {
                    throw UnsupportedOperationException(
                        "Unable to remove the only manager from a family"
                    )
                }
            null -> throw NotFoundException("Member not found")
        }
    }

    private suspend fun removeMember(entity: FamilyMemberEntity): FamilyDetailRead {
        db.familyDao().removeFamilyMember(entity)
        return getFamilyDetail(entity.familyId)
    }
}

// region Conversion
private fun FamilyWithMembers.toFamilyDetail(): FamilyDetailRead {
    val membersInviteesPair = members.partition { it.isConfirmed }
    return FamilyDetailRead(
        id = family.familyId,
        name = family.familyName,
        invitees =
            membersInviteesPair.second.map { it.toMemberDetail() }.takeUnless { it.isEmpty() },
        members = membersInviteesPair.first.map { it.toMemberDetail() },
        createdBy = family.createdBy,
        createdDate = family.createdDate,
        updatedBy = family.updatedBy,
        updatedDate = family.updatedDate
    )
}

private fun FamilyMemberEntity.toMemberDetail() =
    FamilyMemberDetail(
        id = user.userId,
        memberId = memberId,
        name = user.name,
        type = UserType.valueOf(role),
        contactInfo = user.contact?.toContactDetail(),
        invitedBy = invitedBy,
        invitedDate = invitedDate,
        isConfirmed = isConfirmed
    )
// endregion
