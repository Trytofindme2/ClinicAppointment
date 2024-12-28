package org.example.unit22_project.Service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Model.DutyTime;
import org.example.unit22_project.Repository.DutyDateRepo;
import org.example.unit22_project.Repository.DutyTimeRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DutyTimeService
{
    private final DutyTimeRepo dutyTimeRepo;
    private final DutyDateService dutyDateService;

    public DutyTimeService(DutyTimeRepo dutyTimeRepo,
                           DutyDateService dutyDateService){
        this.dutyTimeRepo = dutyTimeRepo;
        this.dutyDateService= dutyDateService;
    }

    public List<DutyTime> findDutyTimeByDoctorId(Long doctorId){
        return dutyTimeRepo.findDutyTimeByDutyDateDoctorId(doctorId);
    }

    public List<DutyTime> findDutyTimeByDoctorIdAndDutyDateId(Long dutyDateId,Long doctorId){
        return dutyTimeRepo.findDutyTimeByDutyDateIdAndDutyDateDoctorId(dutyDateId,doctorId);
    }

    //save duty time info
    public String addDutyTime(Long dutyDateId, LocalTime dutyTime , LocalTime offTime) {
        // Fetch the DutyDate by ID
        DutyDate dutyDate = dutyDateService.findDutyDateById(dutyDateId);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        if (dutyDate.getDate().isEqual(currentDate)) {
            if (dutyTime.isBefore(currentTime)) {
                return "Duty time cannot be earlier than the current time for today's date.";
            }
        } else if (dutyDate.getDate().isBefore(currentDate)) {
            return "Duty time cannot be added for past dates.";
        }
        DutyTime newDutyTime = new DutyTime();
        newDutyTime.setDutyDate(dutyDate);
        newDutyTime.setDutyTime(dutyTime);
        newDutyTime.setOffTime(offTime);
        dutyTimeRepo.save(newDutyTime);
        return "Saved successfully!";
    }

    //delete duty time by id
    public void deleteDutyTimesById(Long dutyTimesId){
        dutyTimeRepo.deleteById(dutyTimesId);
    }







}
