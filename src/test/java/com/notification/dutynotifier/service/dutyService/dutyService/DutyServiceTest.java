package com.notification.dutynotifier.service.dutyService.dutyService;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.exception.EmployeeNotFoundException;
import com.notification.dutynotifier.mapper.DutyMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.service.dutyService.DutyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DutyServiceTest {

    @Mock
    private DutyRepository dutyRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DutyMapper dutyMapper;

    @InjectMocks
    private DutyService dutyService;

    @Test
    void shouldCreateDutySuccessfully() {
        DutyRequest request = new DutyRequest();

        request.setEmployeeIds(List.of(1L));
        request.setDutyDate(LocalDate.now());

        Employee employee = Employee.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();

        Duty duty = Duty.builder()
                .employees(List.of(employee))
                .dutyDate(request.getDutyDate())
                .build();

        DutyResponse response = new DutyResponse(
                1L,
                request.getDutyDate(),
                List.of("Alex"),
                List.of(1L));

        when(employeeRepository.findAllById(request.getEmployeeIds())).thenReturn(List.of(employee));

        when(dutyRepository.save(any(Duty.class))).thenReturn(duty);

        when(dutyMapper.toResponse(duty)).thenReturn(response);

        DutyResponse result = dutyService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(request.getDutyDate(), result.getDutyDate());
        assertEquals(List.of("Alex"), result.getEmployees());

        verify(employeeRepository).findAllById(request.getEmployeeIds());

        verify(dutyRepository).save(any(Duty.class));

        verify(dutyMapper).toResponse(duty);

    }

    @Test
    void shouldThrowUserNotFoundException() {

        DutyRequest request = new DutyRequest();

        request.setEmployeeIds(List.of(1L, 2L));
        request.setDutyDate(LocalDate.now());

        Employee employee = Employee.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .build();

        when(employeeRepository.findAllById(request.getEmployeeIds())).thenReturn(List.of(employee));

        assertThrows(EmployeeNotFoundException.class, () -> dutyService.create(request));

        verify(employeeRepository).findAllById(request.getEmployeeIds());
        verify(dutyRepository, never()).save(any(Duty.class));
    }
}
