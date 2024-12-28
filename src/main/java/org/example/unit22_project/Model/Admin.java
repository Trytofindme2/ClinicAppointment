package org.example.unit22_project.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Admin
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String ReEnterPassword;

    public Admin(){}

    public Admin(Long id, String name, String email, String password, String reEnterPassword) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        ReEnterPassword = reEnterPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getReEnterPassword() {
        return ReEnterPassword;
    }

    public void setReEnterPassword(String reEnterPassword) {
        ReEnterPassword = reEnterPassword;
    }
}

