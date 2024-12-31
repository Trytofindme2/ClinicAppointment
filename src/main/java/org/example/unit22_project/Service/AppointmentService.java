package org.example.unit22_project.Service;

import org.example.unit22_project.Model.Appointment;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.Patient;
import org.example.unit22_project.Repository.AppointmentRepo;
import org.example.unit22_project.Repository.DoctorRepo;
import org.example.unit22_project.Repository.PatientRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AppointmentService
{
    private final AppointmentRepo appointmentRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private static final String PREFIX = "CTS";
    private final Set<String> generatedTickets = new HashSet<>();
    private final Random random = new Random();

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

    public synchronized String generateAppointmentTicket(LocalDate appointmentDate) {
        String ticket;
        do {
            String datePart = appointmentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            int randomPart = random.nextInt(900000) + 100000;
            ticket = String.format("%s-%s-%06d", PREFIX, datePart, randomPart);
        } while (!generatedTickets.add(ticket));
        return ticket;
    }

    public List<Appointment> getAllAppointment(){
        return appointmentRepo.findAll();
    }

    public String SaveAppointment(Long doctorId, Long userId, LocalDate appointmentDate , LocalTime appointmentTime)
    {
        Appointment appointment = new Appointment();
        LocalDate submitDate = LocalDate.now();
        appointment.setDoctor(doctorRepo.findDoctorById(doctorId).get());
        appointment.setPatient(patientRepo.findPatientById(userId).get());
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setSubmitDate(submitDate);
        appointment.setStatus("Pending");
        appointment.setAppointmentTicket(generateAppointmentTicket(appointment.getAppointmentDate()));
        appointmentRepo.save(appointment);
        return "Appointment successfully booked! Please wait for the Admin response.";
    }

    public long getTotalAppointment(){
        return appointmentRepo.count();
    }

    public List<Appointment> searchAppointments(String searchQuery) {
        return appointmentRepo.findAppointmentsByDoctorOrPatientName(searchQuery);
    }

    public List<Appointment>getAppointmentListByDoctorIdAndStatus(Long doctorId)
    {
        return appointmentRepo.findAppointmentByDoctorIdAndStatus(doctorId);
    }

    public List<Appointment>getAppointmentByPatientId(Long patientId){
        return appointmentRepo.findAppointmentByPatientId(patientId);
    }

    public void changeAppointmentStatus(Long appointmentId){
        Appointment appointment = appointmentRepo.findAppointmentById(appointmentId).orElse(null);
        appointment.setStatus("Checked Out");
        appointmentRepo.save(appointment);
    }

    public void changeAppointmentStatusAccept(Long appointmentId){
        Appointment appointment = appointmentRepo.findAppointmentById(appointmentId).orElse(null);
        appointment.setStatus("Accepted");
        appointmentRepo.save(appointment);
    }



}
