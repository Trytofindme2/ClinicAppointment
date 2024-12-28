package org.example.unit22_project.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@Entity
public class DoctorInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String specialization;
    private String qualifications;
    private int experienceYears;
    private double feesRate;

    @Lob
    private byte[] profilePhoto;

    private String phNumber;

    private String address;

    @OneToOne(mappedBy = "doctorInfo")
    @JsonIgnore
    private Doctor doctor;

    public DoctorInfo() {}

    public DoctorInfo(Long id, String specialization, String qualifications, int experienceYears, double feesRate, byte[] profilePhoto, String phNumber, String address, LocalTime dutyTime, LocalTime offTime, Doctor doctor) {
        this.id = id;
        this.specialization = specialization;
        this.qualifications = qualifications;
        this.experienceYears = experienceYears;
        this.feesRate = feesRate;
        this.profilePhoto = profilePhoto;
        this.phNumber = phNumber;
        this.address = address;
        this.doctor = doctor;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public double getFeesRate() {
        return feesRate;
    }

    public void setFeesRate(double feesRate) {
        this.feesRate = feesRate;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}