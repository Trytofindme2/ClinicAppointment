package org.example.unit22_project.AdminServiceTest;

import org.example.unit22_project.Model.Admin;
import org.example.unit22_project.Repository.AdminRepo;
import org.example.unit22_project.Service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminServiceTest
{
    @Mock
    private AdminRepo adminRepo;

    @InjectMocks
    private AdminService adminService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testEncryptAndSaveAdmInfo_Success() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password123");
        admin.setReEnterPassword("password123");

        when(adminRepo.findAdminByEmail(admin.getEmail())).thenReturn(Optional.empty());

        boolean result = adminService.EncryptAndSaveAdmInfo(admin);

        assertTrue(result);
        assertNotEquals("password123", admin.getPassword()); // Ensure password is encrypted
        verify(adminRepo, times(1)).save(admin);
    }

    @Test
    void testEncryptAndSaveAdmInfo_EmailExists() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password123");
        admin.setReEnterPassword("password123");

        when(adminRepo.findAdminByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        boolean result = adminService.EncryptAndSaveAdmInfo(admin);

        assertFalse(result);
        verify(adminRepo, never()).save(admin);
    }

    @Test
    void testEncryptAndSaveAdmInfo_PasswordMismatch() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password123");
        admin.setReEnterPassword("wrongpassword");

        boolean result = adminService.EncryptAndSaveAdmInfo(admin);

        assertFalse(result);
        verify(adminRepo, never()).save(admin);
    }

    @Test
    void testSignInProcessForAdmin_Success() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));

        when(adminRepo.findAdminByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        String result = adminService.SignInProcessForAdmin("admin@example.com", "password123");

        assertEquals("Login successful", result);
    }

    @Test
    void testSignInProcessForAdmin_AccountNotFound() {
        when(adminRepo.findAdminByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        String result = adminService.SignInProcessForAdmin("nonexistent@example.com", "password123");

        assertEquals("Account not found with the provided email.", result);
    }

    @Test
    void testSignInProcessForAdmin_IncorrectPassword() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));

        when(adminRepo.findAdminByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        String result = adminService.SignInProcessForAdmin("admin@example.com", "wrongpassword");

        assertEquals("Incorrect password.", result);
    }

    @Test
    void testFindAdmByEmail_Success() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");

        when(adminRepo.findAdminByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        Admin result = adminService.findAdmByEmail("admin@example.com");

        assertNotNull(result);
        assertEquals("admin@example.com", result.getEmail());
    }

    @Test
    void testFindAdmByEmail_NotFound() {
        when(adminRepo.findAdminByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Admin result = adminService.findAdmByEmail("nonexistent@example.com");

        assertNull(result);
    }

    @Test
    void testFindAdminById_Success() {
        Admin admin = new Admin();
        admin.setId(1L);

        when(adminRepo.findAdminById(1L)).thenReturn(Optional.of(admin));

        Admin result = adminService.findAdminById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindAdminById_NotFound() {
        when(adminRepo.findAdminById(99L)).thenReturn(Optional.empty());

        Admin result = adminService.findAdminById(99L);

        assertNull(result);
    }
}
