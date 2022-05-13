package com.example.monopolymanager.database

import androidx.room.*
import com.example.monopolymanager.entities.User

@Dao
public interface userDao {

    @Query("SELECT * FROM User ORDER BY idUser")
    fun selectAll(): MutableList<User>

    @Query("SELECT * FROM User WHERE username = :username and password = :password")
    fun validate(username: String?, password: String?): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(user: User?)

    @Update
    fun updatePerson(user: User?)

    @Delete
    fun delete(user: User?)

    @Query("SELECT * FROM User WHERE idUser = :id")
    fun loadPersonById(id: Int): User?

    @Query("SELECT * FROM User WHERE username = :username")
    fun loadPersonByUsername(username: String?): User?

    @Query("SELECT COUNT(*) FROM User WHERE username = :username and mail = :mail")
    fun isAvailable(username: String?, mail: String?): Int

    @Query("SELECT COUNT(*) FROM User WHERE mail = :mail")
    fun existingEmails(mail: String?): Int
}