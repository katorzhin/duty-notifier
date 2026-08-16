package com.notification.dutynotifier.service.dutyService.userService;

import com.notification.dutynotifier.dto.response.EmployeeResponse;
import com.notification.dutynotifier.dto.employeeRequest.EmployeeRequest;
import com.notification.dutynotifier.entity.employee.Employee;
import com.notification.dutynotifier.entity.employee.EmployeeStatus;
import com.notification.dutynotifier.mapper.EmployeeMapper;
import com.notification.dutynotifier.repository.dutyRepository.DutyRepository;
import com.notification.dutynotifier.repository.employeeRepository.EmployeeRepository;
import com.notification.dutynotifier.service.auditLogService.AuditLogService;
import com.notification.dutynotifier.service.employeeService.EmployeeService;
import com.notification.dutynotifier.service.securityService.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private DutyRepository dutyRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldCreateEmployee() {
        EmployeeStatus active = EmployeeStatus.ACTIVE;

        EmployeeRequest request = new EmployeeRequest();
        request.setName("Alex");
        request.setEmail("alex@gmail.com");
        request.setStatus(active);

        Employee employee = Employee.builder()
                .id(1L)
                .name("Alex")
                .email("alex@gmail.com")
                .status(active)
                .build();

        EmployeeResponse response = new EmployeeResponse(
                1L,
                "Alex",
                "alex@gmail.com",
                active
        );

        when(employeeMapper.toEntity(request)).thenReturn(employee);

        when(employeeRepository.save(employee)).thenReturn(employee);

        when(employeeMapper.toResponse(employee)).thenReturn(response);
        when(authenticatedUserService.getCurrentUserEmail())
                .thenReturn("alex@gmail.com");

        EmployeeResponse result = employeeService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(employeeMapper).toEntity(request);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).toResponse(employee);
    }
}