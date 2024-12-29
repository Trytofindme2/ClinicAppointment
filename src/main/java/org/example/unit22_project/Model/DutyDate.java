package org.example.unit22_project.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
public class DutyDate
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;


    @OneToMany(mappedBy = "dutyDate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DutyTime> dutyTimes;

    public DutyDate(){}

    public DutyDate(Long id, LocalDate date, Doctor doctor, List<DutyTime> dutyTimes) {
        this.id = id;
        this.date = date;
        this.doctor = doctor;
        this.dutyTimes = dutyTimes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public List<DutyTime> getDutyTimes() {
        return dutyTimes;
    }

    public void setDutyTimes(List<DutyTime> dutyTimes) {
        this.dutyTimes = dutyTimes;
    }
}
