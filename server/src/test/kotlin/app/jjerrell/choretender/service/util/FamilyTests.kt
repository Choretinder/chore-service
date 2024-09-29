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
package app.jjerrell.choretender.service.util

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.jjerrell.choretender.service.database.ChoreServiceDatabase
import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailRead
import app.jjerrell.choretender.service.domain.model.family.FamilyDetailRead
import app.jjerrell.choretender.service.domain.model.user.UserDetailRead
import app.jjerrell.choretender.service.domain.model.user.UserType
import app.jjerrell.choretender.service.domain.repository.*
import app.jjerrell.choretender.service.domain.repository.FamilyRepository
import app.jjerrell.choretender.service.domain.repository.UserRepository
import io.ktor.util.logging.*
import org.junit.After
import org.junit.Before

abstract class FamilyTests {
    protected lateinit var db: ChoreServiceDatabase
    protected val logger: Logger = KtorSimpleLogger("TestLogger")

    @Before
    open fun createDb() {
        db =
            Room.inMemoryDatabaseBuilder<ChoreServiceDatabase>()
                .setDriver(BundledSQLiteDriver())
                .build()
    }

    @After
    open fun closeDb() {
        db.close()
    }

    suspend fun createUser(
        repo: IChoreServiceUserRepository = UserRepository(db, logger),
        name: String = "Test",
        type: UserType = UserType.STANDARD
    ): UserDetailRead {
        return repo.createUser(TestData.userDetailCreate.copy(name = name, type = type))
    }

    suspend fun createFamily(
        repo: IChoreServiceFamilyRepository = FamilyRepository(db, logger),
        invitees: List<Long>? = null
    ): FamilyDetailRead {
        return repo.createFamily(TestData.familyDetailCreate.copy(invitees = invitees))
    }

    suspend fun createChore(
        repo: IChoreServiceChoreRepository = ChoreRepository(db, logger),
        name: String = "Test"
    ): ChoreDetailRead {
        return repo.createChore(
            familyId = TestData.familyDetailRead.id,
            detail = TestData.choreDetailCreate.copy(name = name)
        )
    }
}
