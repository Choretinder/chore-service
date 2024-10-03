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

import app.jjerrell.choretender.service.database.entity.ChoreAssignment
import app.jjerrell.choretender.service.domain.model.chore.ChoreCompletion
import app.jjerrell.choretender.service.util.FamilyTests
import kotlin.test.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock

class ChoreAssignmentDaoTest : FamilyTests() {
    @Test
    fun testInsertChoreAssignment() = runTest {
        // Arrange
        val testFamilyDetail = setupTestFamily()
        val systemTime = Clock.System.now().epochSeconds

        // Act
        val assignmentId =
            db.choreAssignmentDao()
                .insertAssignment(
                    assignment =
                        ChoreAssignment(
                            assigneeId = testFamilyDetail.managerMemberId,
                            choreId = testFamilyDetail.choreId,
                            assignmentStatus = ChoreCompletion.NONE.name,
                            assignmentDate = systemTime
                        )
                )
        val memberWithAssignments =
            db.choreAssignmentDao()
                .getFamilyMemberWithAssignments(assigneeId = testFamilyDetail.managerMemberId)
        val createdAssignment =
            memberWithAssignments.assignments.first { it.assigneeId == assignmentId }

        // Assert
        assertTrue(assignmentId > 0)
        assertEquals(testFamilyDetail.choreId, createdAssignment.choreId)
        assertEquals(ChoreCompletion.NONE.name, createdAssignment.assignmentStatus)
        assertEquals(systemTime, createdAssignment.assignmentDate)
        assertEquals(systemTime, createdAssignment.statusDate)
    }

    @Test
    fun testUpdateChoreAssignment() = runTest {
        // Arrange
        val testFamilyDetail = setupTestFamily()
        val systemTime = Clock.System.now()

        val assignmentId =
            db.choreAssignmentDao()
                .insertAssignment(
                    assignment =
                        ChoreAssignment(
                            assigneeId = testFamilyDetail.managerMemberId,
                            choreId = testFamilyDetail.choreId,
                            assignmentStatus = ChoreCompletion.NONE.name,
                            assignmentDate = systemTime.epochSeconds
                        )
                )
        val memberWithAssignments =
            db.choreAssignmentDao()
                .getFamilyMemberWithAssignments(assigneeId = testFamilyDetail.managerMemberId)
        val createdAssignment =
            memberWithAssignments.assignments.first { it.assigneeId == assignmentId }

        // Act
        val updatedTime = Clock.System.now()
        val updateAssignmentResult =
            db.choreAssignmentDao()
                .updateAssignment(
                    createdAssignment.copy(
                        assignmentStatus = ChoreCompletion.VERIFY.name,
                        statusDate = updatedTime.epochSeconds
                    )
                )

        // Assert
        assertTrue(updateAssignmentResult > 0)
    }

    @Test
    fun testUpdateNonExistentChoreAssignment() = runTest {
        // Arrange
        val testFamilyDetail = setupTestFamily()
        val systemTime = Clock.System.now()

        // Act
        val updateAssignmentResult =
            db.choreAssignmentDao()
                .updateAssignment(
                    ChoreAssignment(
                        assigneeId = testFamilyDetail.managerMemberId,
                        choreId = testFamilyDetail.choreId,
                        assignmentStatus = ChoreCompletion.NONE.name,
                        assignmentDate = systemTime.epochSeconds
                    )
                )

        // Assert
        assertEquals(0, updateAssignmentResult)
    }

    @Test
    fun testRemoveChoreAssignment() = runTest {
        // Arrange
        val testFamilyDetail = setupTestFamily()
        val systemTime = Clock.System.now()

        val assignmentId =
            db.choreAssignmentDao()
                .insertAssignment(
                    assignment =
                        ChoreAssignment(
                            assigneeId = testFamilyDetail.managerMemberId,
                            choreId = testFamilyDetail.choreId,
                            assignmentStatus = ChoreCompletion.NONE.name,
                            assignmentDate = systemTime.epochSeconds
                        )
                )
        val memberWithAssignments =
            db.choreAssignmentDao()
                .getFamilyMemberWithAssignments(assigneeId = testFamilyDetail.managerMemberId)
        val createdAssignment =
            memberWithAssignments.assignments.first { it.assigneeId == assignmentId }

        // Act
        val removeAssignmentResult = db.choreAssignmentDao().removeAssignment(createdAssignment)

        // Assert
        assertTrue(removeAssignmentResult > 0)
    }

    @Test
    fun testRemoveNonExistentChoreAssignment() = runTest {
        // Arrange
        val testFamilyDetail = setupTestFamily()
        val systemTime = Clock.System.now()

        // Act
        val removeAssignmentResult =
            db.choreAssignmentDao()
                .removeAssignment(
                    ChoreAssignment(
                        assigneeId = testFamilyDetail.managerMemberId,
                        choreId = testFamilyDetail.choreId,
                        assignmentStatus = ChoreCompletion.NONE.name,
                        assignmentDate = systemTime.epochSeconds
                    )
                )

        // Assert
        assertEquals(0, removeAssignmentResult)
    }

    @Test
    fun testGetChoreAssignmentsWhenEmpty() = runTest {
        assertFailsWith<IllegalStateException> {
            db.choreAssignmentDao().getFamilyMemberWithAssignments(assigneeId = 0)
        }
    }
}
