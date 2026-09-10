package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAO {

    // Trả về -1 nếu tài khoản đã tồn tại
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertuser(
        user: Entity_user
    ): Long


    // Kiểm tra tên đăng nhập có tồn tại không
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
    suspend fun isUsernameExists(
        tendangnhap: String
    ): Boolean


    // Kiểm tra đăng nhập
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


    // Lấy thông tin bằng tên đăng nhập
    @Query(
        """
        SELECT *
        FROM table_users
        WHERE tendangnhap = :tendangnhap
        LIMIT 1
        """
    )
    suspend fun getUserByUsername(
        tendangnhap: String
    ): Entity_user?


    // Lấy thông tin người dùng bằng ID
    @Query(
        """
        SELECT *
        FROM table_users
        WHERE id = :userId
        LIMIT 1
        """
    )
    suspend fun getUserById(
        userId: Int
    ): Entity_user?


    // Lấy toàn bộ thông tin người dùng
    @Query(
        "SELECT * FROM table_users"
    )
    suspend fun getAllUsers(): List<Entity_user>
}