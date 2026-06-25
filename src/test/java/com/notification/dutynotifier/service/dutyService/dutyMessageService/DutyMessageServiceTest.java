package com.notification.dutynotifier.service.dutyService.dutyMessageService;

import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.user.User;
import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import com.notification.dutynotifier.service.dutyService.DutyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

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

        User user = User.builder()
                .name("Alex")
                .build();

        Duty duty = Duty.builder()
                .users(List.of(user))
                .dutyDate(LocalDate.now())
                .build();

        when(dutyService.findTodayDuties()).thenReturn(List.of(duty));

        String result = dutyMessageService.buildTodayMessage();

        assertTrue(result.contains("Alex"));

        verify(dutyService).findTodayDuties();
    }

    @Test
    void shouldBuildFullMessage() {

        User alex = User.builder()
                .name("Alex")
                .build();

        User ivan = User.builder()
                .name("Ivan")
                .build();

        Duty todayDuty = Duty.builder()
                .dutyDate(LocalDate.now())
                .users(List.of(alex))
                .build();

        Duty nextDuty = Duty.builder()
                .dutyDate(LocalDate.now().plusDays(1))
                .users(List.of(ivan))
                .build();

        when(dutyService.findTodayDuties()).thenReturn(List.of(todayDuty));

        when(dutyService.findNext3DayDuties()).thenReturn(List.of(todayDuty, nextDuty));

        String result = dutyMessageService.buildMessage();

        assertTrue(result.contains("Alex"));
        assertTrue(result.contains("Ivan"));

        verify(dutyService).findTodayDuties();
        verify(dutyService).findNext3DayDuties();
    }
}