package models;

public class User {

    private String uid;
    private String username;
    private String email;
    private String role;

    public User() {
    }

    public User(String uid,
                String username,
                String email,
                String role) {

        this.uid = uid;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}