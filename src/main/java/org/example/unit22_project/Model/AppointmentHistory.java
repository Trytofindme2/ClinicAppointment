package org.example.unit22_project.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class AppointmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private Long appointmentId;
    private LocalDate submitDate;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String status;

    private String appointmentTicket;

    public AppointmentHistory(){}

    public AppointmentHistory(Long id, Doctor doctor, Patient patient, Long appointmentId, LocalDate submitDate, LocalDate appointmentDate, LocalTime appointmentTime, String status, String appointmentTicket) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentId = appointmentId;
        this.submitDate = submitDate;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.appointmentTicket = appointmentTicket;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppointmentTicket() {
        return appointmentTicket;
    }

    public void setAppointmentTicket(String appointmentTicket) {
        this.appointmentTicket = appointmentTicket;
    }
}