package org.example.unit22_project.Model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

@Entity

public class DutyTime
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime dutyTime;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime offTime;

    @ManyToOne
    @JoinColumn(name = "duty_date_id", nullable = false)
    private DutyDate dutyDate;

    public DutyTime(){}
    public DutyTime(Long id, LocalTime dutyTime, LocalTime offTime, DutyDate dutyDate) {
        this.id = id;
        this.dutyTime = dutyTime;
        this.offTime = offTime;
        this.dutyDate = dutyDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getDutyTime() {
        return dutyTime;
    }

    public void setDutyTime(LocalTime dutyTime) {
        this.dutyTime = dutyTime;
    }

    public LocalTime getOffTime() {
        return offTime;
    }

    public void setOffTime(LocalTime offTime) {
        this.offTime = offTime;
    }

    public DutyDate getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(DutyDate dutyDate) {
        this.dutyDate = dutyDate;
    }
}
