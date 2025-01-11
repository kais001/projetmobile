package tn.esprit.finalproject.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "username")
    private String username;

    private String password;

    private boolean emailVerified;

    // Constructor with default emailVerified value
    public User(@NonNull String id, String email, String username, String password, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.emailVerified = emailVerified;
    }

    // Getters and setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
