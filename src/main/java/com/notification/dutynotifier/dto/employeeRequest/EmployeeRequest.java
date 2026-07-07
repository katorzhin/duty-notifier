package com.notification.dutynotifier.dto.employeeRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeRequest {

    @NotBlank
    private String name;

    @Email
    private String email;
}