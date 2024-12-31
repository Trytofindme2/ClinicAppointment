package org.example.unit22_project.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Patient
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String ReEnterPassword;
    private String gender;

    private String address;
    private boolean verified;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    public Patient(){}

    public Patient(Long id, String name, String email, String password, String reEnterPassword, boolean verified,String gender,String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        ReEnterPassword = reEnterPassword;
        this.verified = false;
        this.gender = gender;
        this.address = address;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }
}
