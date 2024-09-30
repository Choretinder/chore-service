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
import app.jjerrell.choretender.service.database.service.ChoreServiceDatabase
import app.jjerrell.choretender.service.domain.model.user.UserType
import app.jjerrell.choretender.service.util.TestData
import io.ktor.util.logging.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {
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
    fun testCreateAndGetUserDetail() = runTest {
        val repo = UserRepository(db, logger)
        val createdUser = repo.createUser(TestData.userDetailCreate)
        val getCreatedUser = repo.getUserDetail(createdUser.id)
        val createdUserTwo = repo.createUser(TestData.userDetailCreate)

        assertEquals(TestData.defaultUser, createdUser)
        assertEquals(createdUser, getCreatedUser)
        assertNotEquals(createdUser, createdUserTwo)
    }

    @Test
    fun testCreateAndUpdateUserDetail() = runTest {
        val repo = UserRepository(db, logger)
        val createdUser = repo.createUser(TestData.userDetailCreate)
        val updatedUser = repo.updateUser(TestData.userDetailUpdate)

        assertNotEquals(createdUser, updatedUser)
        assertEquals(createdUser.id, updatedUser.id)
        assertEquals("Test updated", updatedUser.name)
        assertNull(createdUser.contactInfo)
        assertNull(createdUser.updatedBy)
        assertNull(createdUser.updatedDate)
        assertNotNull(updatedUser.contactInfo)
        assertNotNull(updatedUser.updatedBy)
        assertNotNull(updatedUser.updatedDate)
    }

    @Test
    fun testCreateAndUpdateUserDetailAlt() = runTest {
        val repo = UserRepository(db, logger)
        val createdUser = repo.createUser(TestData.userDetailCreate)
        val updatedUser = repo.updateUser(TestData.userDetailUpdateAlternate)

        assertNotEquals(createdUser, updatedUser)
        assertEquals(createdUser.id, updatedUser.id)
        assertEquals(createdUser.name, updatedUser.name)
        assertEquals(UserType.STANDARD, updatedUser.type)
        assertNull(updatedUser.contactInfo)
        assertNotNull(updatedUser.updatedBy)
        assertNotNull(updatedUser.updatedDate)
    }
}
