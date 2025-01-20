package org.example.unit22_project.DutyDateServiceTest;

import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Repository.DutyDateRepo;
import org.example.unit22_project.Repository.DutyTimeRepo;
import org.example.unit22_project.Service.DoctorService;
import org.example.unit22_project.Service.DutyDateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DutyDateServiceTest {

    @Mock
    private DutyDateRepo dutyDateRepo;

    @Mock
    private DutyTimeRepo dutyTimeRepo;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DutyDateService dutyDateService;

    private Doctor mockDoctor;
    private DutyDate mockDutyDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        mockDoctor.setEmail("doctor@test.com");

        mockDutyDate = new DutyDate();
        mockDutyDate.setId(1L);
        mockDutyDate.setDate(LocalDate.of(2025, 1, 1));
        mockDutyDate.setDoctor(mockDoctor);
    }

    @Test
    void testSaveDutyDate_Success() {
        DutyDate dutyDate = new DutyDate();
        dutyDate.setDate(LocalDate.of(2025, 2, 1));
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        when(doctorService.findDoctorById(1L)).thenReturn(mockDoctor);
        when(dutyDateRepo.save(any(DutyDate.class))).thenReturn(dutyDate);
        String result = dutyDateService.saveDutyDate(dutyDate, 1L);
        assertEquals("Save Successfully", result);
        verify(dutyDateRepo, times(1)).save(dutyDate);
    }

    // Test for saving duty date when the date is expired
    @Test
    void testSaveDutyDate_ExpiredDate() {
        DutyDate dutyDate = new DutyDate();
        dutyDate.setDate(LocalDate.of(2024, 12, 31));
        Doctor mockDoctor = new Doctor();
        mockDoctor.setId(1L);
        when(doctorService.findDoctorById(1L)).thenReturn(mockDoctor);
        when(dutyDateRepo.save(any(DutyDate.class))).thenReturn(dutyDate);
        String result = dutyDateService.saveDutyDate(dutyDate, 1L);
        assertEquals("Your date is already Expired", result);
        verify(dutyDateRepo, never()).save(any(DutyDate.class));
    }

    // Test for saving duty date when doctor ID is null
    @Test
    void testSaveDutyDate_DoctorIdNull() {
        String result = dutyDateService.saveDutyDate(mockDutyDate, null);
        assertEquals("save failed", result);
        verify(dutyDateRepo, never()).save(any(DutyDate.class));
    }

    // Test for finding duty date by ID when found
    @Test
    void testFindDutyDateById_Success() {
        when(dutyDateRepo.findById(1L)).thenReturn(Optional.of(mockDutyDate));
        DutyDate result = dutyDateService.findDutyDateById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // Test for finding duty date by ID when not found
    @Test
    void testFindDutyDateById_NotFound() {
        when(dutyDateRepo.findById(1L)).thenReturn(Optional.empty());
        DutyDate result = dutyDateService.findDutyDateById(1L);
        assertNull(result);
    }

    // Test for showing all duty dates by doctor ID
    @Test
    void testShowAllDutyDateByDoctorId() {
        when(dutyDateRepo.findDutyDateByDoctor_Id(1L)).thenReturn(List.of(mockDutyDate));
        List<DutyDate> result = dutyDateService.showAllDutyDateByDoctorId(1L);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // Test for showing all duty dates by doctor ID when no data
    @Test
    void testShowAllDutyDateByDoctorId_NoData() {
        when(dutyDateRepo.findDutyDateByDoctor_Id(1L)).thenReturn(Collections.emptyList());
        List<DutyDate> result = dutyDateService.showAllDutyDateByDoctorId(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test for deleting duty date by ID
    @Test
    void testDeleteDutyDateById() {
        Long dutyDateId = 1L;
        doNothing().when(dutyDateRepo).deleteById(dutyDateId);
        dutyDateService.deleteDutyDateById(dutyDateId);
        verify(dutyDateRepo, times(1)).deleteById(dutyDateId);
    }

    // Test for deleting expired duty dates on startup
    @Test
    void testDeleteExpiredDutyDatesOnStartup() {
        LocalDate currentDate = LocalDate.now();
        DutyDate expiredDutyDate = new DutyDate();
        expiredDutyDate.setId(1L);
        expiredDutyDate.setDate(LocalDate.of(2024, 12, 31));
        List<DutyDate> expiredDutyDates = List.of(expiredDutyDate);
        when(dutyDateRepo.findExpiredDutyDates(currentDate)).thenReturn(expiredDutyDates);
        doNothing().when(dutyTimeRepo).deleteByDutyDateId(anyLong());
        doNothing().when(dutyDateRepo).deleteAll(anyList());
        dutyDateService.deleteExpiredDutyDatesOnStartup();
        verify(dutyTimeRepo, times(1)).deleteByDutyDateId(anyLong());
        verify(dutyDateRepo, times(1)).deleteAll(expiredDutyDates);
    }

    @Test
    void testCheckDutyDate_ValidDate() {
        LocalDate futureDate = LocalDate.of(2025, 2, 1);
        boolean result = dutyDateService.checkDutyDate(futureDate);
        assertTrue(result);
    }

    // Test for checkDutyDate method - expired (past) date
    @Test
    void testCheckDutyDate_ExpiredDate() {
        boolean result = dutyDateService.checkDutyDate(LocalDate.of(2024, 12, 31));
        assertFalse(result);
    }
}
