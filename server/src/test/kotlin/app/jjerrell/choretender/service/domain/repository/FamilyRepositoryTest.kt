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

import app.jjerrell.choretender.service.domain.model.family.FamilyMemberChangeRole
import app.jjerrell.choretender.service.domain.model.family.FamilyMemberLeave
import app.jjerrell.choretender.service.domain.model.family.FamilyMemberVerify
import app.jjerrell.choretender.service.domain.model.user.UserType
import app.jjerrell.choretender.service.util.FamilyTests
import app.jjerrell.choretender.service.util.TestData
import io.ktor.server.plugins.*
import io.ktor.util.logging.*
import kotlin.test.*
import kotlinx.coroutines.test.runTest

class FamilyRepositoryTest : FamilyTests() {
    @Test
    fun testCreateFamily() = runTest {
        // Arrange
        // == Create a standard user
        val createdUser = createUser()

        // == Create the family
        val createdFamily = createFamily()

        // Test
        assertEquals(TestData.familyDetailRead.id, createdFamily.id)
        assertEquals(TestData.familyDetailRead.name, createdFamily.name)
        assertEquals(TestData.familyDetailRead.invitees, createdFamily.invitees)
        assertEquals(TestData.familyDetailRead.createdBy, createdFamily.createdBy)
        assertEquals(TestData.familyDetailRead.createdDate, createdFamily.createdDate)
        assertEquals(TestData.familyDetailRead.updatedBy, createdFamily.updatedBy)
        assertEquals(TestData.familyDetailRead.updatedDate, createdFamily.updatedDate)

        // Validate the user, expected to be a manager of the family
        val creator = createdFamily.members.first()
        assertEquals(createdUser.id, creator.id)
        assertEquals(1, creator.memberId)
        assertEquals(createdUser.name, creator.name)
        assertEquals(UserType.MANAGER, creator.type)
        assertEquals(createdUser.contactInfo, creator.contactInfo)
        assertEquals(0, creator.invitedBy)
        assertEquals(true, creator.isConfirmed)
    }

    @Test
    fun testCreateFamilyWithInvitee() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        val createdUser = createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val createdFamily = createFamily(invitees = listOf(secondUser.id))

        // Test
        val creator = createdFamily.members.first()
        val invitedUser = createdFamily.invitees?.firstOrNull()
        assertNotNull(invitedUser)
        assertEquals(secondUser.id, invitedUser.id)
        assertEquals(2, invitedUser.memberId)
        assertEquals(secondUser.name, invitedUser.name)
        assertEquals(UserType.STANDARD, invitedUser.type)
        assertEquals(secondUser.contactInfo, invitedUser.contactInfo)
        assertEquals(creator.memberId, invitedUser.invitedBy)
        assertEquals(false, invitedUser.isConfirmed)
    }

    @Test
    fun testCreateFamilyWithDuplicateInvitee() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        val createdUser = createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val createdFamily = createFamily(invitees = listOf(secondUser.id, secondUser.id))

        // Test
        assert(createdFamily.invitees?.count() == 1)
    }

    @Test
    fun testVerifyFamilyMember() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        val createdUser = createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo, invitees = listOf(secondUser.id))

        val inviteeMemberId = createdFamily.invitees?.firstOrNull()?.memberId
        assertNotNull(inviteeMemberId)

        // == Verify the invitee: `secondUser`
        val updatedFamily =
            familyRepo.verifyFamilyMember(
                familyId = createdFamily.id,
                detail = FamilyMemberVerify(memberId = inviteeMemberId)
            )

        // Test
        assert(updatedFamily.invitees.isNullOrEmpty())
        assert(updatedFamily.members.count() == 2)
        assert(updatedFamily.members.all { it.isConfirmed })
    }

    @Test
    fun testVerifyMissingFamilyMember() = runTest {
        // Arrange
        // == Create a standard user
        createUser()

        // == Setup the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo)

        // Test
        assertFailsWith(NotFoundException::class) {
            familyRepo.verifyFamilyMember(
                familyId = createdFamily.id,
                detail = FamilyMemberVerify(memberId = 2)
            )
        }
    }

    @Test
    fun testVerifyExisingFamilyMember() = runTest {
        // Arrange
        // == Create a standard user
        createUser()

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo)

        // Test
        assertFailsWith(NotFoundException::class) {
            familyRepo.verifyFamilyMember(
                familyId = createdFamily.id,
                detail = FamilyMemberVerify(memberId = 1)
            )
        }
    }

    @Test
    fun testPromoteFamilyMember() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        val createdUser = createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo, invitees = listOf(secondUser.id))

        val inviteeMemberId = createdFamily.invitees?.firstOrNull()?.memberId
        assertNotNull(inviteeMemberId)

        // == Verify the invitee: `secondUser`
        familyRepo.verifyFamilyMember(
            familyId = createdFamily.id,
            detail = FamilyMemberVerify(memberId = inviteeMemberId)
        )

        // == Promote the invitee
        val updatedFamily =
            familyRepo.changeMemberRole(
                familyId = createdFamily.id,
                detail = FamilyMemberChangeRole(memberId = inviteeMemberId, role = UserType.MANAGER)
            )

        // Test
        assert(updatedFamily.members.all { it.type == UserType.MANAGER })
    }

    @Test
    fun testChangeFamilyMemberRole() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        val createdUser = createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo, invitees = listOf(secondUser.id))

        val inviteeMemberId = createdFamily.invitees?.firstOrNull()?.memberId
        assertNotNull(inviteeMemberId)

        // Test
        // == Member is unconfirmed
        assertFailsWith(NotFoundException::class) {
            familyRepo.changeMemberRole(
                familyId = createdFamily.id,
                detail = FamilyMemberChangeRole(memberId = inviteeMemberId, role = UserType.MANAGER)
            )
        }

        // == Member does not exist
        assertFailsWith(NotFoundException::class) {
            familyRepo.changeMemberRole(
                familyId = createdFamily.id,
                detail = FamilyMemberChangeRole(memberId = 3, role = UserType.MANAGER)
            )
        }
    }

    @Test
    fun testDemoteSingleMemberManager() = runTest {
        // Arrange
        // == Create a standard user
        createUser()

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo)

        val owningMember = createdFamily.members.singleOrNull { it.type == UserType.MANAGER }

        // Act/Assert
        assertNotNull(owningMember)
        assertFailsWith<UnsupportedOperationException> {
            familyRepo.changeMemberRole(
                familyId = createdFamily.id,
                detail =
                    FamilyMemberChangeRole(
                        memberId = owningMember.memberId,
                        role = UserType.STANDARD
                    )
            )
        }
    }

    @Test
    fun testRemoveSingleMemberManager() = runTest {
        // Arrange
        // == Create a standard user
        createUser()

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo)
        val owningMember = createdFamily.members.singleOrNull { it.type == UserType.MANAGER }

        // Act/Assert
        assertNotNull(owningMember)
        assertFailsWith<UnsupportedOperationException> {
            familyRepo.leaveFamilyGroup(
                familyId = createdFamily.id,
                detail = FamilyMemberLeave(memberId = owningMember.memberId)
            )
        }
    }

    @Test
    fun testRemoveInviteeOrStandardFamilyMember() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo, invitees = listOf(secondUser.id))

        val inviteeMemberId = createdFamily.invitees?.firstOrNull()?.memberId
        assertNotNull(inviteeMemberId)

        // Act
        val updatedFamily =
            familyRepo.leaveFamilyGroup(
                familyId = createdFamily.id,
                detail = FamilyMemberLeave(memberId = inviteeMemberId)
            )

        // Assert
        assertNull(updatedFamily.invitees)
        assert(updatedFamily.members.count() == 1)
    }

    @Test
    fun testRemoveManagerOfCoManagedFamily() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo, invitees = listOf(secondUser.id))

        val inviteeMemberId = createdFamily.invitees?.firstOrNull()?.memberId
        assertNotNull(inviteeMemberId)

        // == Verify the invitee: `secondUser`
        familyRepo.verifyFamilyMember(
            familyId = createdFamily.id,
            detail = FamilyMemberVerify(memberId = inviteeMemberId)
        )

        // == Promote the invitee
        familyRepo.changeMemberRole(
            familyId = createdFamily.id,
            detail = FamilyMemberChangeRole(memberId = inviteeMemberId, role = UserType.MANAGER)
        )

        // Act
        // == Remove the original member
        val updatedFamily =
            familyRepo.leaveFamilyGroup(
                familyId = createdFamily.id,
                detail = FamilyMemberLeave(memberId = 1)
            )

        // Assert
        assert(updatedFamily.members.count() == 1)
        assert(updatedFamily.members.first().memberId == 2L)
    }

    @Test
    fun testRemoveNonExistentMember() = runTest {
        // Arrange
        // == Create standard users
        val userRepo = UserRepository(db, logger)
        createUser(userRepo)
        val secondUser = createUser(userRepo, name = "Test User 2")

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = createFamily(familyRepo, invitees = listOf(secondUser.id))

        // Act/Assert
        assertFailsWith<NotFoundException> {
            familyRepo.leaveFamilyGroup(
                familyId = createdFamily.id,
                detail = FamilyMemberLeave(memberId = 3)
            )
        }
    }
}
