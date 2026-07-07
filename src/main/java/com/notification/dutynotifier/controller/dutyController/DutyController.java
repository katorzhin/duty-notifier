package com.notification.dutynotifier.controller.dutyController;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import com.notification.dutynotifier.service.dutyService.DutyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/duties")
@RequiredArgsConstructor
public class DutyController {

    private final DutyService dutyService;
    private final DutyMessageService dutyMessageService;

    @PostMapping
    public DutyResponse create(@RequestBody @Valid DutyRequest request) {
        return dutyService.create(request);
    }

    @GetMapping
    public Page<DutyResponse> getAll(

            @RequestParam(required = false)
            LocalDate from,

            @RequestParam(required = false)
            LocalDate to,

            @RequestParam(required = false)
            List<Long> employeeIds,

            @PageableDefault(
                    sort = "dutyDate",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return dutyService.getAll(from, to, employeeIds, pageable);
    }

    @PutMapping("/{id}")
    public DutyResponse update(@PathVariable Long id, @RequestBody @Valid DutyRequest request) {
        return dutyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dutyService.delete(id);
    }

    @GetMapping("/today")
    public List<DutyResponse> getToday() {
        return dutyService.getTodayDuty();
    }

    @GetMapping("/next3")
    public List<DutyResponse> getNext3() {
        return dutyService.getNext3Days();
    }

    @GetMapping("/message")
    public String message() {
        return dutyMessageService.buildMessage();
    }

}