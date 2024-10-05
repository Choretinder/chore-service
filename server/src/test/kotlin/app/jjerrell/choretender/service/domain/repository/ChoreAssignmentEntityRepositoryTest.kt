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

import app.jjerrell.choretender.service.domain.model.chore.ChoreCompletion
import app.jjerrell.choretender.service.util.FamilyTests
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChoreAssignmentEntityRepositoryTest : FamilyTests() {
    @Test
    fun testAssignAndUpdateAssignment() = runTest {
        // Arrange
        val familySetup = setupTestFamily()
        val repo = ChoreAssignmentRepository(db = db, logger = logger)
        val assignment =
            repo.assignChore(id = familySetup.choreId, assignee = familySetup.managerMemberId)

        // Act
        val updatedAssignment =
            repo.updateAssignment(
                assignmentId = assignment.assignmentId,
                status = ChoreCompletion.VERIFY
            )

        // Assert
        assertEquals(ChoreCompletion.VERIFY, updatedAssignment.assignmentStatus)
        assertTrue { updatedAssignment.statusDate > updatedAssignment.assignmentDate }
    }

    @Test
    fun testAssignAndGetAssignments() = runTest {
        // Arrange
        val familySetup = setupTestFamily()
        val inviteeChore = createChore(name = "Test two")

        // == Assign the chores
        val repo = ChoreAssignmentRepository(db = db, logger = logger)
        val managerChoreAssignment =
            repo.assignChore(familySetup.choreId, assignee = familySetup.managerMemberId)
        val inviteeChoreAssignment =
            repo.assignChore(inviteeChore.id, assignee = familySetup.inviteeMemberId)

        // Act - Get member assignments
        val managerChores = repo.getChoreAssignments(familySetup.managerMemberId)
        val memberChores = repo.getChoreAssignments(familySetup.inviteeMemberId)

        // Assert
        assertContains(managerChores, managerChoreAssignment.assignment)
        assertContains(memberChores, inviteeChoreAssignment.assignment)
    }

    @Test
    fun testAssignExistingAssignment() = runTest {
        // Arrange
        val familySetup = setupTestFamily()

        // == Assign the chore
        val repo = ChoreAssignmentRepository(db = db, logger = logger)
        val managerChoreAssignment =
            repo.assignChore(familySetup.choreId, assignee = familySetup.managerMemberId)
        // == Attempt to assign is to someone else
        val updatedChore =
            repo.assignChore(familySetup.choreId, assignee = familySetup.inviteeMemberId)

        // Act - Get member assignments
        val managerChores = repo.getChoreAssignments(familySetup.managerMemberId)
        val memberChores = repo.getChoreAssignments(familySetup.inviteeMemberId)

        // Assert
        assertContains(managerChores, managerChoreAssignment.assignment)
        assertEquals(managerChoreAssignment, updatedChore)
        assertFalse { memberChores.contains(updatedChore.assignment) }
    }
}
