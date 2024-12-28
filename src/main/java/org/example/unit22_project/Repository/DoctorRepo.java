package org.example.unit22_project.Repository;

import org.example.unit22_project.Model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor,Long>
{
    Optional<Doctor>findDoctorById(Long id);

    Optional<Doctor>findDoctorByEmail(String email);

    List<Doctor> findDoctorByVerified(boolean status);

    long count();

    long countByVerified(boolean status);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.doctorInfo WHERE d.doctorInfo.specialization = :specialization")
    List<Doctor> findDoctorsWithInfoBySpecialization(@Param("specialization") String specialization);

    List<Doctor> findAll();

    @Query("SELECT d FROM Doctor d JOIN FETCH d.dutyDates dd WHERE dd.date = :localDate")
    List<Doctor> searchDoctorByDutyDates(@Param("localDate") LocalDate date);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.doctorInfo.specialization) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Doctor> searchDoctorsByNameOrSpecialization(@Param("query") String query);

    @Query("SELECT d FROM Doctor d JOIN d.dutyDates dutyDate WHERE dutyDate.date = :date")
    List<Doctor> findDoctorByDutyDates(@Param("date") LocalDate date);



}
