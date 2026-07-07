package com.notification.dutynotifier.mapper;

import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.entity.duty.Duty;
import com.notification.dutynotifier.entity.employee.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DutyMapper {

    @Mapping(target = "employees", expression = "java(mapEmployees(duty))")
    @Mapping(target = "employeeIds", expression = "java(mapEmployeeIds(duty))")

    DutyResponse toResponse(Duty duty);

    default List<String> mapEmployees(Duty duty) {
        return duty.getEmployees()
                .stream()
                .map(Employee::getName)
                .toList();
    }

    default List<Long> mapEmployeeIds(Duty duty) {
        return duty.getEmployees()
                .stream()
                .map(Employee::getId)
                .toList();
    }
}