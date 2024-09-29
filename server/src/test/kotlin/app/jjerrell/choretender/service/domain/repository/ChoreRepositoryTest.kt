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

import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailUpdate
import app.jjerrell.choretender.service.util.FamilyTests
import app.jjerrell.choretender.service.util.TestData
import io.ktor.util.logging.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class ChoreRepositoryTest : FamilyTests() {
    @Test
    fun testCreateChore() = runTest {
        // Arrange
        // == Setup a standard user and family
        createUser()
        createFamily()

        // Act
        // == Create a standard chore
        val createdChore = createChore()

        // Assert
        assertEquals(TestData.choreDetailReadOne, createdChore)
    }

    @Test
    fun testReadMultipleChores() = runTest {
        // Arrange
        // == Setup a standard user and family with chores
        createUser()
        createFamily()
        val choreRepo = ChoreRepository(db, logger)
        val createdChore = createChore(choreRepo)
        val secondCreatedChore = createChore(choreRepo, name = "Test Two")

        val allFamilyChores = choreRepo.getFamilyChoreDetails(familyId = 1)

        // Assert
        assertContentEquals(
            expected = listOf(createdChore, secondCreatedChore),
            actual = allFamilyChores
        )
    }

    @Test
    fun testUpdateChore() = runTest {
        // Arrange
        // == Setup a standard user and family with a chore
        createUser()
        val createdFamily = createFamily()

        val choreRepo = ChoreRepository(db, logger)
        createChore(choreRepo)

        // Act
        // == Update the created chore
        val updatedChore =
            choreRepo.updateChore(familyId = createdFamily.id, detail = TestData.choreDetailUpdate)

        // Assert
        assertEquals(TestData.choreDetailReadOneUpdated, updatedChore)
    }

    @Test
    fun testUpdateChoreWithAlternateChanges() = runTest {
        // Arrange
        // == Setup a standard user and family with a chore
        createUser()
        val createdFamily = createFamily()

        val choreRepo = ChoreRepository(db, logger)
        val createdChore = createChore(choreRepo)

        // Act
        // == Update the created chore
        val updatedChore =
            choreRepo.updateChore(
                familyId = createdFamily.id,
                detail =
                    ChoreDetailUpdate(
                        id = createdChore.id,
                        name = createdChore.name,
                        recurrence = createdChore.recurrence,
                        endDate = createdChore.endDate,
                        status = createdChore.status,
                        updatedDate = 1,
                        updatedBy = 1
                    )
            )

        // Assert
        assertEquals(TestData.choreDetailReadOne.copy(updatedBy = 1, updatedDate = 1), updatedChore)
    }
}
