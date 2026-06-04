package com.notification.dutynotifier.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DutyResponse {

    private Long id;

    private LocalDate dutyDate;

    private List<String> users;
}