package com.example.finals_new;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userinfo")
public class UserInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String password;
    private String phonenumber;
    private String email;
    private String defaultcity;

    public UserInfo(String username, String password, String phonenumber, String email, String defaultcity) {
        this.username = username;
        this.password = password;
        this.phonenumber = phonenumber;
        this.email = email;
        this.defaultcity = defaultcity;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public String getDefaultcity() {
        return defaultcity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDefaultcity(String defaultcity) {
        this.defaultcity = defaultcity;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", email='" + email + '\'' +
                ", defaultcity='" + defaultcity + '\'' +
                '}';
    }
}
