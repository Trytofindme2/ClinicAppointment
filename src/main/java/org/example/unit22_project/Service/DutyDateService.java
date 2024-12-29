package org.example.unit22_project.Service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Model.DutyTime;
import org.example.unit22_project.Repository.DutyDateRepo;
import org.example.unit22_project.Repository.DutyTimeRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DutyDateService
{
    private final DutyDateRepo dutyDateRepo;

    private final DoctorService doctorService;

    private final DutyTimeRepo dutyTimeRepo;

    public DutyDateService(DutyDateRepo dutyDateRepo,
                           DoctorService doctorService,
                           DutyTimeRepo dutyTimeRepo){
        this.dutyDateRepo = dutyDateRepo;
        this.doctorService = doctorService;
        this.dutyTimeRepo =dutyTimeRepo;
    }

    //save doctor duty date info
    public String saveDutyDate(DutyDate dutyDate,Long doctorId){
        if(doctorId != null)
        {
            if(checkDutyDate(dutyDate.getDate())){
                dutyDate.setDoctor(doctorService.findDoctorById(doctorId));
                dutyDateRepo.save(dutyDate);
                return "Save Successfully";
            }
            return "Your date is already Expired";
        }
        else {
            return "save failed";
        }
    }

    //find duty date by id
    public DutyDate findDutyDateById(Long id){
        Optional<DutyDate> dutyDateOptional = dutyDateRepo.findById(id);
        if(dutyDateOptional.isPresent()){
            DutyDate dutyDate = dutyDateOptional.get();
            return dutyDate;
        }
        return null;
    }

    //show all duty date by doctorId
    public List<DutyDate> showAllDutyDateByDoctorId(Long doctorId){
        if(doctorId != null){
            return dutyDateRepo.findDutyDateByDoctor_Id(doctorId);
        }
        return null;
    }

    //delete duty date by duty id
    public void deleteDutyDateById(Long dutyDateId){
        dutyDateRepo.deleteById(dutyDateId);
    }

    //delete the previous date
    @Transactional
    @PostConstruct
    public void deleteExpiredDutyDatesOnStartup() {
        LocalDate currentDate = LocalDate.now();
        List<DutyDate> expiredDutyDates = dutyDateRepo.findExpiredDutyDates(currentDate);
        for (DutyDate expiredDutyDate : expiredDutyDates) {
            dutyTimeRepo.deleteByDutyDateId(expiredDutyDate.getId());
        }
        dutyDateRepo.deleteAll(expiredDutyDates);
    }


    public boolean checkDutyDate(LocalDate input_localDate){
        LocalDate currentDate = LocalDate.now();
        if(input_localDate.isBefore(currentDate)){
            return false;
        }
        else {
            return true;
        }
    }
}
