package org.example.unit22_project.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Doctor
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String reEnterPassword;
    private String gender;

    private boolean verified;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_info_id", referencedColumnName = "id")
    @JsonManagedReference
    private DoctorInfo doctorInfo;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DutyDate> dutyDates;

    public Doctor(){}

    public Doctor(Long id, String name, String email, String password, String reEnterPassword, String phoneNumber, String gender, DoctorInfo doctorInfo ,
                  boolean verified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.reEnterPassword = reEnterPassword;
        this.gender = gender;
        this.doctorInfo = doctorInfo;
        this.verified = false;
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
        return reEnterPassword;
    }

    public void setReEnterPassword(String reEnterPassword) {
        this.reEnterPassword = reEnterPassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public DoctorInfo getDoctorInfo() {
        return doctorInfo;
    }

    public void setDoctorInfo(DoctorInfo doctorInfo) {
        this.doctorInfo = doctorInfo;
    }

    public void setVerified(boolean verified){
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }
}
