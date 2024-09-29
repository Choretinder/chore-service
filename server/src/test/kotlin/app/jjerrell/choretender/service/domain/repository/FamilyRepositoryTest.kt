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

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.jjerrell.choretender.service.database.ChoreServiceDatabase
import app.jjerrell.choretender.service.domain.model.family.FamilyMemberChangeRole
import app.jjerrell.choretender.service.domain.model.family.FamilyMemberVerify
import app.jjerrell.choretender.service.domain.model.user.UserType
import app.jjerrell.choretender.service.util.TestData
import io.ktor.server.plugins.*
import io.ktor.util.logging.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before

class FamilyRepositoryTest {
    private lateinit var db: ChoreServiceDatabase
    private val logger: Logger = KtorSimpleLogger("TestLogger")

    @Before
    fun createDb() {
        db =
            Room.inMemoryDatabaseBuilder<ChoreServiceDatabase>()
                .setDriver(BundledSQLiteDriver())
                .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testCreateFamily() = runTest {
        // Arrange
        // == Create a standard user
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = familyRepo.createFamily(TestData.familyDetailCreate)

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
        // == Create a standard user
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))
        val secondUser = userRepo.createUser(TestData.userDetailCreate.copy(name = "Test User 2"))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily =
            familyRepo.createFamily(
                TestData.familyDetailCreate.copy(invitees = listOf(secondUser.id))
            )

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
        // == Create a standard user
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))
        val secondUser = userRepo.createUser(TestData.userDetailCreate.copy(name = "Test User 2"))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily =
            familyRepo.createFamily(
                TestData.familyDetailCreate.copy(invitees = listOf(secondUser.id, secondUser.id))
            )

        // Test
        assert(createdFamily.invitees?.count() == 1)
    }

    @Test
    fun testVerifyFamilyMember() = runTest {
        // Arrange
        // == Create a standard user
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))
        val secondUser = userRepo.createUser(TestData.userDetailCreate.copy(name = "Test User 2"))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily =
            familyRepo.createFamily(
                TestData.familyDetailCreate.copy(invitees = listOf(secondUser.id))
            )

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
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = familyRepo.createFamily(TestData.familyDetailCreate)

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
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily = familyRepo.createFamily(TestData.familyDetailCreate)

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
        // == Create a standard user
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))
        val secondUser = userRepo.createUser(TestData.userDetailCreate.copy(name = "Test User 2"))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily =
            familyRepo.createFamily(
                TestData.familyDetailCreate.copy(invitees = listOf(secondUser.id))
            )

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
    fun testPromoteUnconfirmedOrMissingFamilyMember() = runTest {
        // Arrange
        // == Create a standard user
        val userRepo = UserRepository(db, logger)
        val createdUser =
            userRepo.createUser(TestData.userDetailCreate.copy(type = UserType.STANDARD))
        val secondUser = userRepo.createUser(TestData.userDetailCreate.copy(name = "Test User 2"))

        // == Create the family
        val familyRepo = FamilyRepository(db, logger)
        val createdFamily =
            familyRepo.createFamily(
                TestData.familyDetailCreate.copy(invitees = listOf(secondUser.id))
            )

        val inviteeMemberId = createdFamily.invitees?.firstOrNull()?.memberId
        assertNotNull(inviteeMemberId)

        // Test
        assertFailsWith(NotFoundException::class) {
            familyRepo.changeMemberRole(
                familyId = createdFamily.id,
                detail = FamilyMemberChangeRole(memberId = inviteeMemberId, role = UserType.MANAGER)
            )
        }

        assertFailsWith(NotFoundException::class) {
            familyRepo.changeMemberRole(
                familyId = createdFamily.id,
                detail = FamilyMemberChangeRole(memberId = 3, role = UserType.MANAGER)
            )
        }
    }
}
