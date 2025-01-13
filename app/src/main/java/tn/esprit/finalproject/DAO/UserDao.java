package tn.esprit.finalproject.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.finalproject.Entity.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email AND password = :password")
    int login(String email, String password);

    @Query("SELECT * FROM users WHERE id = :id")
    User findById(long id);


    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Delete
    void delete(User user);

    @Query("UPDATE users SET email = :email WHERE id = :id")
    void updateEmail(int id, String email);

    @Query("UPDATE users SET password = :password WHERE id = :id")
    void updatePassword(int id, String password);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE (email = :identifier OR username = :identifier) AND password = :password")
    User findByEmailOrUsernameee(String identifier, String password);

    @Query("SELECT * FROM users WHERE email = :identifier OR username = :identifier LIMIT 1")
    User findByEmailOrUsername(String identifier);

    @Query("SELECT * FROM users WHERE email = :email OR username = :username LIMIT 1")
    User findByEmailOrUsernameUnique(String email, String username);

    @Query("SELECT * FROM users WHERE username = :username")
    User findByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :userId")
    User findByUserId(long userId);
}
