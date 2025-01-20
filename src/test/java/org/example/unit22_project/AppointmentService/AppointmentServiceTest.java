package org.example.unit22_project.AppointmentService;
import org.example.unit22_project.Model.Appointment;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.Patient;
import org.example.unit22_project.Repository.AppointmentHistoryRepo;
import org.example.unit22_project.Repository.AppointmentRepo;
import org.example.unit22_project.Repository.DoctorRepo;
import org.example.unit22_project.Repository.PatientRepo;
import org.example.unit22_project.Service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AppointmentServiceTest
{
    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private AppointmentHistoryRepo appointmentHistoryRepo;

    @Mock
    private PatientRepo patientRepo;

    @Mock
    private DoctorRepo doctorRepo;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsAppointmentSlotAvailable() {
        Long doctorId = 1L;
        LocalDate appointmentDate = LocalDate.of(2025, 1, 20);
        LocalTime appointmentTime = LocalTime.of(10, 0);

        when(appointmentRepo.countByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId, appointmentDate, appointmentTime))
                .thenReturn(6L);

        boolean result = appointmentService.isAppointmentSlotAvailable(doctorId, appointmentDate, appointmentTime);

        assertTrue(result);
        verify(appointmentRepo, times(1))
                .countByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId, appointmentDate, appointmentTime);
    }

    @Test
    void testGenerateAppointmentTicket() {
        LocalDate appointmentDate = LocalDate.of(2025, 1, 20);

        String ticket = appointmentService.generateAppointmentTicket(appointmentDate);

        assertNotNull(ticket);
        assertTrue(ticket.startsWith("CTS-20250120-"));
    }

    @Test
    void testSaveAppointment() {
        Long doctorId = 1L;
        Long userId = 2L;
        LocalDate appointmentDate = LocalDate.of(2025, 1, 22);
        LocalTime appointmentTime = LocalTime.of(14, 30);

        Doctor mockDoctor = new Doctor();
        Patient mockPatient = new Patient();
        mockDoctor.setId(doctorId);
        mockPatient.setId(userId);

        when(doctorRepo.findDoctorById(doctorId)).thenReturn(Optional.of(mockDoctor));
        when(patientRepo.findPatientById(userId)).thenReturn(Optional.of(mockPatient));

        String result = appointmentService.SaveAppointment(doctorId, userId, appointmentDate, appointmentTime);

        assertEquals("Appointment successfully booked! Please wait for the Admin response.", result);
        verify(appointmentRepo, times(1)).save(any(Appointment.class));
    }

    @Test
    void testSendTicketProcess() {
        Long appointmentId = 1L;
        String appointmentStatus = "Accepted";

        Appointment mockAppointment = new Appointment();
        mockAppointment.setAppointmentDate(LocalDate.of(2025, 1, 22));

        when(appointmentRepo.findAppointmentById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        String result = appointmentService.sendTicketProcess(appointmentStatus, appointmentId);

        assertEquals("Send Appointment Ticket To the Patient", result);
        verify(appointmentRepo, times(1)).save(mockAppointment);
    }

    @Test
    void testDeleteAppointmentAndSaveToHistory() {
        Long userId = 1L;
        Long doctorId = 2L;
        Long appointmentId = 3L;

        Patient mockPatient = new Patient();
        Doctor mockDoctor = new Doctor();
        Appointment mockAppointment = new Appointment();
        mockPatient.setId(userId);
        mockDoctor.setId(doctorId);
        mockAppointment.setId(appointmentId);

        when(patientRepo.findPatientById(userId)).thenReturn(Optional.of(mockPatient));
        when(doctorRepo.findDoctorById(doctorId)).thenReturn(Optional.of(mockDoctor));
        when(appointmentRepo.findAppointmentById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        appointmentService.DeleteAppointmentAndSaveToHistory(userId, doctorId, appointmentId);

        verify(appointmentHistoryRepo, times(1)).save(any());
        verify(appointmentRepo, times(1)).deleteAppointmentByPatientId(userId);
    }

}
