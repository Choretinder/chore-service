package app.jjerrell.choretender.service.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.jjerrell.choretender.service.database.dao.UserDao
import app.jjerrell.choretender.service.database.entity.UserEntity
import kotlinx.coroutines.Dispatchers

@Database(entities = [UserEntity::class], version = 1)
abstract class ChoreServiceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

object ChoreDatabaseBuilder {
    fun build(): ChoreServiceDatabase = Room
        .databaseBuilder<ChoreServiceDatabase>(name = "chore-service")
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}