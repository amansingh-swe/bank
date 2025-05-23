package org.aman.bankapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}