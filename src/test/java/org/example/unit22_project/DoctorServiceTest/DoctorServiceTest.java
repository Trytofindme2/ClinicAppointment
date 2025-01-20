package org.example.unit22_project.DoctorServiceTest;

import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Repository.AppointmentRepo;
import org.example.unit22_project.Repository.DoctorRepo;
import org.example.unit22_project.Service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DoctorServiceTest
{
    @Mock
    private DoctorRepo doctorRepo;

    @Mock
    private AppointmentRepo appointmentRepo;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("test@example.com");
        doctor.setPassword("password123");
        doctor.setReEnterPassword("password123");
        doctor.setVerified(false);
    }

    @Test
    void testPasswordsMatch() {
        doctor.setReEnterPassword(doctor.getPassword());
        assertTrue(doctorService.passwordsMatch(doctor));
    }

    @Test
    void testIsEmailAvailable() {
        when(doctorRepo.findDoctorByEmail(doctor.getEmail())).thenReturn(Optional.empty());
        assertTrue(doctorService.isEmailAvailable(doctor.getEmail()));

        when(doctorRepo.findDoctorByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));
        assertFalse(doctorService.isEmailAvailable(doctor.getEmail()));
    }

    @Test
    void testEncodeAndSaveDoctorInfo() {
        when(doctorRepo.save(any(Doctor.class))).thenReturn(doctor);

        boolean result = doctorService.encodeAndSaveDoctorInfo(doctor);
        assertTrue(result);
        verify(doctorRepo, times(1)).save(any(Doctor.class));
    }

    @Test
    void testSignInProcess() {
        when(doctorRepo.findDoctorByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        String result = doctorService.signInProcess(doctor.getEmail(), "wrongPassword");
        assertEquals("Incorrect password.", result);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        doctor.setPassword(encoder.encode("password123"));

        result = doctorService.signInProcess(doctor.getEmail(), "password123");
        assertEquals("Your account is not verified.", result);

        doctor.setVerified(true);
        result = doctorService.signInProcess(doctor.getEmail(), "password123");
        assertNull(result);
    }

    @Test
    void testFindDoctorByVerifiedStatus() {
        List<Doctor> unverifiedDoctors = List.of(doctor);
        when(doctorRepo.findDoctorByVerified(false)).thenReturn(unverifiedDoctors);

        List<Doctor> result = doctorService.findDoctorByUnverified();
        assertEquals(1, result.size());
        assertFalse(result.get(0).isVerified());
    }

    @Test
    void testChangeVerifiedStatus() {
        when(doctorRepo.findDoctorById(doctor.getId())).thenReturn(Optional.of(doctor));

        doctorService.changeVerifiedStatus(doctor.getId());

        assertTrue(doctor.isVerified());
        verify(doctorRepo, times(1)).save(doctor);
    }

    @Test
    void testDeleteDoctorById() {
        when(doctorRepo.findDoctorById(doctor.getId())).thenReturn(Optional.of(doctor));

        doctorService.deleteDoctorById(doctor.getId());

        verify(doctorRepo, times(1)).deleteById(doctor.getId());
    }

    @Test
    void testChangePassword() {
        when(doctorRepo.findDoctorById(doctor.getId())).thenReturn(Optional.of(doctor));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        doctor.setPassword(encoder.encode("oldPassword"));

        String result = doctorService.changePassword(doctor.getId(), "wrongOldPassword", "newPassword", "newPassword");
        assertEquals("Old password is incorrect.", result);

        result = doctorService.changePassword(doctor.getId(), "oldPassword", "newPassword", "mismatchPassword");
        assertEquals("New passwords do not match.", result);

        result = doctorService.changePassword(doctor.getId(), "oldPassword", "newPassword", "newPassword");
        assertEquals("Reset Password is Done", result);
        verify(doctorRepo, times(1)).save(doctor);
    }

    @Test
    void testGetDoctorCount() {
        when(doctorRepo.count()).thenReturn(10L);
        long result = doctorService.getDoctorCount();
        assertEquals(10L, result);
    }

    @Test
    void testFindDoctorBySpecialization() {
        when(doctorRepo.findAll()).thenReturn(List.of(doctor));
        when(doctorRepo.findDoctorsWithInfoBySpecialization("cardiology")).thenReturn(List.of(doctor));

        List<Doctor> allDoctors = doctorService.findDoctorBySpecialization("all");
        assertEquals(1, allDoctors.size());

        List<Doctor> specializedDoctors = doctorService.findDoctorBySpecialization("cardiology");
        assertEquals(1, specializedDoctors.size());
    }

    @Test
    void testGetDoctorsByDutyDate() {
        LocalDate date = LocalDate.now();
        when(doctorRepo.findDoctorByDutyDates(date)).thenReturn(List.of(doctor));

        List<Doctor> result = doctorService.getDoctorsByDutyDate(date);
        assertEquals(1, result.size());
    }
}
