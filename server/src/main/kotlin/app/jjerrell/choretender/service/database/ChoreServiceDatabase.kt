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
package app.jjerrell.choretender.service.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.jjerrell.choretender.service.database.dao.FamilyDao
import app.jjerrell.choretender.service.database.dao.UserDao
import app.jjerrell.choretender.service.database.entity.FamilyEntity
import app.jjerrell.choretender.service.database.entity.FamilyMemberEntity
import app.jjerrell.choretender.service.database.entity.UserEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [UserEntity::class, FamilyEntity::class, FamilyMemberEntity::class],
    version = 5
)
abstract class ChoreServiceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun familyDao(): FamilyDao
}

object ChoreDatabaseBuilder {
    fun build(): ChoreServiceDatabase =
        Room.databaseBuilder<ChoreServiceDatabase>(name = "chore-service")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
}
