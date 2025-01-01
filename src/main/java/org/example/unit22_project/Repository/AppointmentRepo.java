package org.example.unit22_project.Repository;

import org.example.unit22_project.Model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment,Long>
{

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.appointmentTime = :appointmentTime")
    long countByDoctorIdAndAppointmentDateAndAppointmentTime(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("appointmentTime") LocalTime appointmentTime
    );

    long countByDoctorId(long doctorId);

    Optional<Appointment>findAppointmentById(Long appointmentId);

    List<Appointment>findAppointmentByPatientId(Long id);

    void deleteAppointmentByPatientId(Long userId);


    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND (a.status = 'Pending' OR a.status = 'Accepted' OR a.status = 'Successfully Send Ticket')")
    List<Appointment> findAppointmentByDoctorIdAndStatus(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE (a.doctor.name LIKE %:searchQuery% OR a.patient.name LIKE %:searchQuery%)")
    List<Appointment> findAppointmentsByDoctorOrPatientName(@Param("searchQuery") String searchQuery);






}
