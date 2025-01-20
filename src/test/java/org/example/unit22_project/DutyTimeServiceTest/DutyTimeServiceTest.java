package org.example.unit22_project.DutyTimeServiceTest;


import org.example.unit22_project.Model.DutyDate;
import org.example.unit22_project.Model.DutyTime;
import org.example.unit22_project.Model.DutyTimeDTO;
import org.example.unit22_project.Repository.DutyDateRepo;
import org.example.unit22_project.Repository.DutyTimeRepo;
import org.example.unit22_project.Service.DutyDateService;
import org.example.unit22_project.Service.DutyTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class DutyTimeServiceTest
{
    @Mock
    private DutyTimeRepo dutyTimeRepo;

    @Mock
    private DutyDateService dutyDateService;

    @Mock
    private DutyDateRepo dutyDateRepo;

    @InjectMocks
    private DutyTimeService dutyTimeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddDutyTime_ForCurrentDateAndValidTime() {
        DutyDate dutyDate = new DutyDate();
        dutyDate.setDate(LocalDate.now());
        dutyDate.setId(1L);

        DutyTime dutyTime = new DutyTime();
        dutyTime.setDutyDate(dutyDate);
        dutyTime.setDutyTime(LocalTime.of(10, 0));
        dutyTime.setOffTime(LocalTime.of(18, 0));

        when(dutyDateService.findDutyDateById(1L)).thenReturn(dutyDate);
        when(dutyTimeRepo.save(any(DutyTime.class))).thenReturn(dutyTime);

        String result = dutyTimeService.addDutyTime(1L, LocalTime.of(10, 0), LocalTime.of(18, 0));
        assertEquals("Saved successfully!", result);
        verify(dutyTimeRepo, times(1)).save(any(DutyTime.class));
    }

    @Test
    void testAddDutyTime_PastDate() {
        DutyDate dutyDate = new DutyDate();
        dutyDate.setDate(LocalDate.of(2023, 12, 31));
        dutyDate.setId(1L);

        when(dutyDateService.findDutyDateById(1L)).thenReturn(dutyDate);

        String result = dutyTimeService.addDutyTime(1L, LocalTime.of(10, 0), LocalTime.of(18, 0));
        assertEquals("Duty time cannot be added for past dates.", result);
    }

    @Test
    void testAddDutyTime_BeforeCurrentTimeForToday() {
        DutyDate dutyDate = new DutyDate();
        dutyDate.setDate(LocalDate.now());
        dutyDate.setId(1L);
        LocalTime currentTime = LocalTime.now();  // Get actual current time
        LocalTime dutyTime = currentTime.minusMinutes(10);
        LocalTime offTime = dutyTime.plusHours(5);
        when(dutyDateService.findDutyDateById(1L)).thenReturn(dutyDate);
        String result = dutyTimeService.addDutyTime(1L, dutyTime, offTime);
        assertEquals("Duty time cannot be earlier than the current time for today's date.", result);
    }

    @Test
    void testGetDutyTimes() {
        DutyDate dutyDate = new DutyDate();
        dutyDate.setDate(LocalDate.of(2025, 1, 1));
        dutyDate.setId(1L);

        DutyTime dutyTime1 = new DutyTime();
        dutyTime1.setDutyTime(LocalTime.of(9, 0));
        dutyTime1.setOffTime(LocalTime.of(17, 0));
        dutyTime1.setDutyDate(dutyDate);

        DutyTime dutyTime2 = new DutyTime();
        dutyTime2.setDutyTime(LocalTime.of(10, 0));
        dutyTime2.setOffTime(LocalTime.of(18, 0));
        dutyTime2.setDutyDate(dutyDate);

        when(dutyTimeRepo.findDutyTimesByDateAndDoctor(LocalDate.of(2025, 1, 1), 1L))
                .thenReturn(List.of(dutyTime1, dutyTime2));

        List<DutyTimeDTO> result = dutyTimeService.getDutyTimes(LocalDate.of(2025, 1, 1), 1L);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteDutyTimesById() {
        DutyTime dutyTime = new DutyTime();
        dutyTime.setId(1L);

        dutyTimeService.deleteDutyTimesById(1L);
        verify(dutyTimeRepo, times(1)).deleteById(1L);
    }
}
