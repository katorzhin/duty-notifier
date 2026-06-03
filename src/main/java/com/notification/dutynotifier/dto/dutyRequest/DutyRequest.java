package com.notification.dutynotifier.dto.dutyRequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DutyRequest {

    @NotEmpty
    private List<Long> userIds;

    @NotNull
    private LocalDate dutyDate;
}