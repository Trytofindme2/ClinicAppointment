package org.example.unit22_project.Model;

import java.time.LocalTime;

public class DutyTimeDTO {
    private LocalTime dutyTime;
    private LocalTime offTime;

    // Constructor
    public DutyTimeDTO(LocalTime dutyTime, LocalTime offTime) {
        this.dutyTime = dutyTime;
        this.offTime = offTime;
    }

    // Getters and Setters
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
}
