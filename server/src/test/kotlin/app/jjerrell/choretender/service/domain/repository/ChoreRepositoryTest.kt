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
import io.ktor.util.logging.*
import org.junit.After
import org.junit.Before

class ChoreRepositoryTest {
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
}
