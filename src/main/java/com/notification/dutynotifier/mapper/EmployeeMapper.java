package com.notification.dutynotifier.mapper;

import com.notification.dutynotifier.dto.response.EmployeeResponse;
import com.notification.dutynotifier.dto.employeeRequest.EmployeeRequest;
import com.notification.dutynotifier.entity.employee.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    Employee toEntity(EmployeeRequest request);
    EmployeeResponse toResponse(Employee employee);
}