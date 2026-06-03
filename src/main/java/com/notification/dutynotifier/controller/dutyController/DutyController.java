package com.notification.dutynotifier.controller.dutyController;

import com.notification.dutynotifier.bot.DutyBot;
import com.notification.dutynotifier.dto.dutyRequest.DutyRequest;
import com.notification.dutynotifier.entity.duty.Duty;
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
    private final DutyBot dutyBot;


    @PostMapping
    public Duty create(
            @RequestBody
            @Valid
            DutyRequest request) {
        return dutyService.create(request);
    }

    @GetMapping
    public List<Duty> getAll() {
        return dutyService.getAll();
    }

    @GetMapping("/today")
    public List<Duty> getToday() {
        return dutyService.getTodayDuty();
    }

    @GetMapping("/next5")
    public List<Duty> getNext5() {
        return dutyService.getNext5Days();
    }
    @GetMapping("/message")
    public String message() {
        return dutyService.buildMessage();
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    public String upload(
            @RequestParam("file")
            MultipartFile file
    ) {

        dutyService.importExcel(file);

        return "Schedule uploaded";
    }

    @GetMapping("/send")
    public String send() {

        dutyBot.sendMessage(
                508025069L,
                dutyService.buildMessage());

        return "sent";
    }

}