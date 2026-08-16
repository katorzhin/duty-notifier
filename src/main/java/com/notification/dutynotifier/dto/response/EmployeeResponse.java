package com.notification.dutynotifier.dto.response;

import com.notification.dutynotifier.entity.employee.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;

    private String name;

    private String email;

    private EmployeeStatus status;
}