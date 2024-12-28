package org.example.unit22_project.Repository;

import jakarta.transaction.Transactional;
import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Model.DutyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DutyTimeRepo extends JpaRepository<DutyTime,Long>
{
    Optional<DutyTime> findById(Long dutyDateId);

    List<DutyTime> findDutyTimeByDutyDateDoctorId(Long doctorId);

    List<DutyTime> findDutyTimeByDutyDateIdAndDutyDateDoctorId(Long doctorId,Long dutyDateId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DutyTime dt WHERE dt.dutyDate.date = :currentDate AND dt.dutyTime < :currentTime")
    void deletePastDutyTimes(@Param("currentDate") LocalDate currentDate, @Param("currentTime") LocalTime currentTime);


    @Modifying
    @Transactional
    @Query("DELETE FROM DutyTime t WHERE t.dutyDate.id = :dutyDateId")
    void deleteByDutyDateId(@Param("dutyDateId") Long dutyDateId);
}
