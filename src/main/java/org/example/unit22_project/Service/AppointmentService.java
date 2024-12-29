package org.example.unit22_project.Service;

import org.example.unit22_project.Model.Appointment;
import org.example.unit22_project.Model.AppointmentDTO;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.Patient;
import org.example.unit22_project.Repository.AppointmentRepo;
import org.example.unit22_project.Repository.DoctorRepo;
import org.example.unit22_project.Repository.PatientRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService
{
    private final AppointmentRepo appointmentRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;

    public AppointmentService(AppointmentRepo appointmentRepo,
                              PatientRepo patientRepo,
                              DoctorRepo doctorRepo){
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo =doctorRepo;
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
        LocalDate submitDate = LocalDate.now();
        appointment.setDoctorId(doctorId);
        appointment.setUserId(userId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setSubmitDate(submitDate);
        appointmentRepo.save(appointment);
        return "Appointment successfully booked! Please wait for the Admin response.";
    }

    public long getTotalAppointment(){
        return appointmentRepo.count();
    }

    public List<AppointmentDTO> getAllAppointmentsWithNames() {
        List<Appointment> appointments = appointmentRepo.findAll();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); // 12-hour format with AM/PM

        return appointments.stream().map(appointment -> {
            Doctor doctor = doctorRepo.findById(appointment.getDoctorId()).orElse(null);
            Patient patient = patientRepo.findById(appointment.getUserId()).orElse(null);

            String doctorName = doctor != null ? doctor.getName() : "Unknown Doctor";
            String patientName = patient != null ? patient.getName() : "Unknown Patient";
            String formattedTime = appointment.getAppointmentTime().format(timeFormatter);

            return new AppointmentDTO(
                    appointment.getId(),
                    doctorName,
                    patientName,
                    appointment.getAppointmentDate(),
                    formattedTime,
                    appointment.isPermission()
            );
        }).collect(Collectors.toList());
    }


}
