package com.notification.dutynotifier.dto.dutyRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;


@Data
public class GenerateDutyRequest {

    @NotNull
    private LocalDate startDate;

    @Min(1)
    private Integer days;

    @Min(1)
    private Integer employeesPerDuty;

    @NotEmpty
    private List<Long> employeeIds;
}