package com.notification.dutynotifier.controller.uploadController;

import com.notification.dutynotifier.service.excelImportService.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final ExcelImportService excelImportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(
            @RequestParam("file")
            MultipartFile file,

            @RequestParam(defaultValue = "false")
            boolean replace) {

        excelImportService.importExcel(file, replace);

        return "Schedule uploaded";
    }
}