package org.example.unit22_project.Repository;

import jakarta.transaction.Transactional;
import org.example.unit22_project.Model.DutyDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DutyDateRepo extends JpaRepository<DutyDate,Long>
{
    List<DutyDate> findDutyDateByDoctor_Id(Long doctorId);

    @Transactional
    @Modifying
    @Query("DELETE FROM DutyDate d WHERE d.date < :currentDate ")
    void deleteExpiredDutyDate(@Param("currentDate") LocalDate currentDate);


    @Query("SELECT d FROM DutyDate d WHERE d.date < :currentDate")
    List<DutyDate> findExpiredDutyDates(@Param("currentDate") LocalDate currentDate);

}
