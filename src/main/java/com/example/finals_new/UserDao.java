package com.example.finals_new;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUserInfo(UserInfo userinfo);

    @Query("SELECT * FROM userinfo")
    List<UserInfo> findAll();

    @Query("SELECT * FROM userinfo where username = :username")
    UserInfo findUserInfoByusername(String username);

    @Query("SELECT defaultcity FROM userinfo where username = :username")
    String findDefaultcityByusername(String username);

    @Query("DELETE FROM userinfo where username = :username")
    void deleteUserInfo(String username);

    @Update
    void updateUserInfoByusername(UserInfo userInfo);


}
