package org.example.unit22_project.PatientServiceTest;

import org.example.unit22_project.Model.Patient;
import org.example.unit22_project.Repository.PatientRepo;
import org.example.unit22_project.Service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PatientServiceTest
{
    @Mock
    private PatientRepo patientRepo;

    @InjectMocks
    private PatientService patientService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setEmail("test@example.com");
        patient.setPassword("password123");
        patient.setReEnterPassword("password123");
    }

    @Test
    void testCheckPasswordAndReEnterPassword_Success() {
        assertTrue(patientService.checkPasswordAndReEnterPassword(patient));
    }

    @Test
    void testCheckPasswordAndReEnterPassword_Failure() {
        patient.setReEnterPassword("wrongpassword");
        assertFalse(patientService.checkPasswordAndReEnterPassword(patient));
    }

    @Test
    void testFindExitedUser_UserExists() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.of(patient));
        assertTrue(patientService.findExitedUser(patient.getEmail()));
    }

    @Test
    void testFindExitedUser_UserNotExists() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.empty());
        assertFalse(patientService.findExitedUser(patient.getEmail()));
    }

    @Test
    void testFindPatientByEmail_UserExists() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.of(patient));
        assertNotNull(patientService.findPatientByEmail(patient.getEmail()));
    }

    @Test
    void testFindPatientByEmail_UserNotExists() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.empty());
        assertNull(patientService.findPatientByEmail(patient.getEmail()));
    }

    @Test
    void testEncryptAndSaveInfo_Success() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.empty());
        when(patientRepo.save(patient)).thenReturn(patient);
        assertTrue(patientService.EncryptAndSaveInfo(patient));
    }

    @Test
    void testEncryptAndSaveInfo_Failure_ExistingUser() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.of(patient));
        assertFalse(patientService.EncryptAndSaveInfo(patient));
    }

    @Test
    void testEncryptAndSaveInfo_Failure_PasswordMismatch() {
        patient.setReEnterPassword("wrongpassword");
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.empty());
        assertFalse(patientService.EncryptAndSaveInfo(patient));
    }

    @Test
    void testFindUserByUnVerifiedStatus() {
        when(patientRepo.findPatientByVerified(false)).thenReturn(List.of(patient));
        assertEquals(1, patientService.findUserByUnVerifiedStatus().size());
    }

    @Test
    void testUserLogInProcess_UserNotFound() {
        when(patientRepo.findPatientByEmail(patient.getEmail())).thenReturn(Optional.empty());
        assertEquals("Account not found with the provided email.", patientService.UserLogInProcess(patient.getEmail(), patient.getPassword()));
    }


    @Test
    void testUserLogInProcess_IncorrectPassword() {
        String email = patient.getEmail();
        when(patientRepo.findPatientByEmail(email)).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("wrongPassword", patient.getPassword())).thenReturn(false); // Mock password mismatch
        String result = patientService.UserLogInProcess(email, "wrongPassword");
        assertEquals("Incorrect password.", result);
    }

    @Test
    void testUserLogInProcess_InvalidEmail() {
        String invalidEmail = "invalid@example.com";
        when(patientRepo.findPatientByEmail(invalidEmail)).thenReturn(Optional.empty()); // Mock email not found
        String result = patientService.UserLogInProcess(invalidEmail, "anyPassword");
        assertEquals("Account not found with the provided email.", result);
    }

    @Test
    void testGetUserCount() {
        when(patientRepo.count()).thenReturn(5L);
        assertEquals(5L, patientService.getUserCount());
    }

    @Test
    void testGetUnverifiedUserCount() {
        when(patientRepo.countByVerified(false)).thenReturn(2L);
        assertEquals(2L, patientService.getUnverifiedUserCount());
    }

    @Test
    void testChangeVerifiedStatus() {
        when(patientRepo.findPatientById(1L)).thenReturn(Optional.of(patient));
        patientService.changeVerifiedStatus(1L);
        verify(patientRepo, times(1)).save(patient);
    }

    @Test
    void testRejectRequest() {
        when(patientRepo.findPatientById(1L)).thenReturn(Optional.of(patient));
        patientService.rejectRequest(1L);
        verify(patientRepo, times(1)).deleteById(1L);
    }
}
