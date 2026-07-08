package com.example.EnglishWithStork.RoomDatabase.Entity

import  androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAO {

    //Tra ve -1 neu tai khoan da ton tai.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertuser(user: Entity_user): Long

    // Kiem tra ten dang nhap co ton tai khong ?
    @Query(
"""
    SELECT EXISTS
        (
        SELECT 1
        FROM table_users
        WHERE tendangnhap = :tendangnhap
        )
        """
    )

    suspend fun isUsernameExists(tendangnhap: String): Boolean
    //kiem tra dang nhap
    @Query(
        """
            SELECT *
            FROM table_users
            WHERE tendangnhap = :tendangnhap
            AND matkhau = :matkhau
            LIMIT 1
        """
    )
    suspend fun login(
        tendangnhap: String,
        matkhau: String
    ): Entity_user?

    //Lay thong tin bang ten dang nhap
    @Query(
        """
            SELECT *
            FROM table_users
            WHERE tendangnhap = :tendangnhap
            LIMIT 1
        """
    )
    suspend fun getUserByUsername(tendangnhap: String): Entity_user?
    //Lay toan bo thong tin nguoi dung
    @Query("SELECT * FROM table_users")
    suspend fun getAllUsers(): List<Entity_user>
}