package org.example.unit22_project.DoctorInfoServiceTest;

import org.example.unit22_project.Model.Doctor;
import org.example.unit22_project.Model.DoctorInfo;
import org.example.unit22_project.Repository.DoctorInfoRepo;
import org.example.unit22_project.Repository.DoctorRepo;
import org.example.unit22_project.Service.DoctorInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DoctorInfoServiceTest
{
    private DoctorInfoRepo doctorInfoRepo;
    private DoctorRepo doctorRepo;
    private DoctorInfoService doctorInfoService;

    @BeforeEach
    void setUp() {
        doctorInfoRepo = mock(DoctorInfoRepo.class);
        doctorRepo = mock(DoctorRepo.class);
        doctorInfoService = new DoctorInfoService(doctorInfoRepo, doctorRepo);
    }

    @Test
    void testSaveDoctorInfo() {
        DoctorInfo doctorInfo = new DoctorInfo();
        doctorInfoService.saveDoctorInfo(doctorInfo);
        verify(doctorInfoRepo, times(1)).save(doctorInfo);
    }

    @Test
    void testCheckDoctorInfoExists() {
        Long doctorId = 1L;

        when(doctorInfoRepo.findByDoctor_Id(doctorId)).thenReturn(Optional.of(new DoctorInfo()));

        boolean result = doctorInfoService.checkDoctorInfo(doctorId);
        assertTrue(result);

        verify(doctorInfoRepo, times(1)).findByDoctor_Id(doctorId);
    }

    @Test
    void testUpdateDoctorInfo() {
        Long doctorId = 1L;
        DoctorInfo updatedInfo = new DoctorInfo();
        updatedInfo.setPhNumber("123456789");
        updatedInfo.setFeesRate(500);

        DoctorInfo existingInfo = new DoctorInfo();
        existingInfo.setPhNumber("987654321");
        existingInfo.setFeesRate(300);

        when(doctorInfoRepo.findByDoctor_Id(doctorId)).thenReturn(Optional.of(existingInfo));

        doctorInfoService.updateDoctorInfo(updatedInfo, doctorId);

        assertEquals("123456789", existingInfo.getPhNumber());
        assertEquals(500, existingInfo.getFeesRate());
        verify(doctorInfoRepo, times(1)).save(existingInfo);
    }

    @Test
    void testDeleteDoctorInfo() {
        Long doctorId = 1L;

        doctorInfoService.deleteDoctorInfoById(doctorId);

        verify(doctorRepo, times(1)).deleteById(doctorId);
    }
}
