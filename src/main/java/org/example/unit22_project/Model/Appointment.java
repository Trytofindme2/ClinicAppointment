package org.example.unit22_project.Model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Appointment
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


    private LocalDate submitDate;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String status;

    private String appointmentTicket;

    public Appointment(Long id, Doctor doctor,
                       Patient patient,
                       LocalDate submitDate,
                       LocalDate appointmentDate,
                       LocalTime appointmentTime,
                       String appointmentTicket,
                       String status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.submitDate = submitDate;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.appointmentTicket = appointmentTicket;
    }

    public Appointment() {

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

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDate submitDate) {
        this.submitDate = submitDate;
    }

    public String getAppointmentTicket() {
        return appointmentTicket;
    }

    public void setAppointmentTicket(String appointmentTicket) {
        this.appointmentTicket = appointmentTicket;
    }
}
