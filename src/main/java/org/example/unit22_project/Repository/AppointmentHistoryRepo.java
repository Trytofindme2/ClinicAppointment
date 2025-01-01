package org.example.unit22_project.Repository;

import org.example.unit22_project.Model.Appointment;
import org.example.unit22_project.Model.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentHistoryRepo extends JpaRepository<AppointmentHistory,Long>
{

    @Query("SELECT a FROM AppointmentHistory a " +
            "WHERE (a.doctor.name LIKE %:searchQuery% OR a.patient.name LIKE %:searchQuery%)")
    List<AppointmentHistory> findAppointmentHistoryByDoctorOrPatientName(@Param("searchQuery") String searchQuery);


    List<AppointmentHistory>findAppointmentHistoriesByPatientId(Long patientId);
}
