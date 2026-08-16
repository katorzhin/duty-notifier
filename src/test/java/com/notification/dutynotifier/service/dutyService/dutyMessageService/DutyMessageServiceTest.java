package com.notification.dutynotifier.service.dutyService.dutyMessageService;

import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import com.notification.dutynotifier.service.dutyService.DutyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DutyMessageServiceTest {

    @Mock
    private DutyService dutyService;

    @InjectMocks
    private DutyMessageService dutyMessageService;

    @Test
    void shouldBuildTodayMessage() {

        Employee employee = Employee.builder()
                .name("Alex")
                .build();

        Duty duty = Duty.builder()
                .employees(List.of(employee))
                .dutyDate(LocalDate.now())
                .build();

        when(dutyService.findTodayDuties()).thenReturn(List.of(duty));

        String result = dutyMessageService.buildTodayMessage("{{TODAY}}");

        assertTrue(result.contains("Alex"));

        verify(dutyService).findTodayDuties();
    }

    @Test
    void shouldBuildScheduleMessage() {

        Employee ivan = Employee.builder()
                .name("Ivan")
                .build();

        Duty todayDuty = Duty.builder()
                .dutyDate(LocalDate.now())
                .employees(List.of(ivan))
                .build();

        Duty nextDuty = Duty.builder()
                .dutyDate(LocalDate.now().plusDays(1))
                .employees(List.of(ivan))
                .build();

        when(dutyService.findNext3DayDuties())
                .thenReturn(List.of(todayDuty, nextDuty));

        String result = dutyMessageService.buildScheduleMessage("""
            📅 Наступні чергування:
            
            {{NEXT_DUTIES}}
            """);

        assertTrue(result.contains("Ivan"));
        assertFalse(result.contains("{{NEXT_DUTIES}}"));

        verify(dutyService).findNext3DayDuties();
    }
}