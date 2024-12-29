package org.example.unit22_project.Service;

import org.example.unit22_project.Model.Appointment;
import org.example.unit22_project.Repository.AppointmentRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService
{
    private final AppointmentRepo appointmentRepo;

    public AppointmentService(AppointmentRepo appointmentRepo){
        this.appointmentRepo = appointmentRepo;
    }

    public boolean isAppointmentSlotAvailable(Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime) {
        return appointmentRepo.countByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId, appointmentDate, appointmentTime) < 7;
    }


    public List<Appointment> getAllAppointment(){
        return appointmentRepo.findAll();
    }

    public String SaveAppointment(Long doctorId, Long userId, LocalDate appointmentDate , LocalTime appointmentTime)
    {
        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctorId);
        appointment.setUserId(userId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointmentRepo.save(appointment);
        return "Appointment Saved Successfully";
    }

}
