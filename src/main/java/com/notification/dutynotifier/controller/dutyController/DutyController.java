package com.notification.dutynotifier.controller.dutyController;

import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.dto.response.DutyResponse;
import com.notification.dutynotifier.service.dutyMessageService.DutyMessageService;
import com.notification.dutynotifier.service.excelImportService.ExcelImportService;
import com.notification.dutynotifier.service.dutyService.DutyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/duties")
@RequiredArgsConstructor
public class DutyController {

    private final DutyService dutyService;
    private final DutyMessageService dutyMessageService;
    private final ExcelImportService excelImportService;

    @PostMapping
    public DutyResponse create(@RequestBody @Valid DutyRequest request) {
        return dutyService.create(request);
    }

    @GetMapping
    public List<DutyResponse> getAll() {
        return dutyService.getAll();
    }

    @GetMapping("/today")
    public List<DutyResponse> getToday() {
        return dutyService.getTodayDuty();
    }

    @GetMapping("/next5")
    public List<DutyResponse> getNext5() {
        return dutyService.getNext5Days();
    }

    @GetMapping("/message")
    public String message() {
        return dutyMessageService.buildMessage();
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public String upload(@RequestParam("file") MultipartFile file) {
        excelImportService.importExcel(file);

        return "Schedule uploaded";
    }
}