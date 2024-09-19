package app.jjerrell.choretender.service.database.dao

import androidx.room.*
import app.jjerrell.choretender.service.database.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(category: UserEntity)

    @Query("SELECT * FROM userEntity")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM userEntity WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity

    @Query("DELETE FROM userEntity WHERE id = :id")
    suspend fun deleteUserById(id: Int)
}
