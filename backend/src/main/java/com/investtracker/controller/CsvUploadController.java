package com.investtracker.controller;

import com.investtracker.service.CsvUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class CsvUploadController {

    private final CsvUploadService csvUploadService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadCsv(
        @RequestParam("file") MultipartFile file,
        @RequestHeader("X-Tenant-Id") String tenantId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("Received CSV upload request for tenant: {} by user: {}", tenantId, userId);
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));
        }

        try {
            int count = csvUploadService.uploadInvestments(file, tenantId, userId);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully processed " + count + " records",
                "count", count
            ));
        } catch (Exception e) {
            log.error("CSV Upload failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Failed to process CSV: " + e.getMessage()
            ));
        }
    }
}
