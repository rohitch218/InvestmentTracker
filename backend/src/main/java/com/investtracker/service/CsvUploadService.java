package com.investtracker.service;

import com.investtracker.dto.request.InvestmentRequest;
import com.investtracker.entity.InvestmentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * CsvUploadService — Refactored for Phase 4 (Microservices Parity).
 * 
 * Instead of calling the local InvestmentRepository, this service now 
 * propagates CSV data to the 'portfolio-service' via REST.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CsvUploadService {

    private final RestTemplate restTemplate;

    @Value("${app.portfolio-service-url}")
    private String portfolioServiceUrl;

    public int uploadInvestments(MultipartFile file, String tenantId, Long userId) {
        int count = 0;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                 CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setIgnoreHeaderCase(true)
                     .build())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                try {
                    InvestmentRequest request = new InvestmentRequest();
                    request.setName(csvRecord.get("Name"));
                    request.setType(InvestmentType.valueOf(csvRecord.get("Type").toUpperCase()));
                    
                    if (csvRecord.isMapped("Symbol") && !csvRecord.get("Symbol").isEmpty()) {
                        request.setSymbol(csvRecord.get("Symbol"));
                    }
                    
                    request.setQuantity(new BigDecimal(csvRecord.get("Quantity")));
                    request.setPurchasePrice(new BigDecimal(csvRecord.get("PurchasePrice")));
                    
                    if (csvRecord.isMapped("CurrentPrice") && !csvRecord.get("CurrentPrice").isEmpty()) {
                        request.setCurrentPrice(new BigDecimal(csvRecord.get("CurrentPrice")));
                    } else {
                        request.setCurrentPrice(request.getPurchasePrice());
                    }
                    
                    request.setPurchaseDate(LocalDate.parse(csvRecord.get("PurchaseDate")));
                    if (csvRecord.isMapped("Notes")) {
                        request.setNotes(csvRecord.get("Notes"));
                    }

                    // REST Call to Portfolio Service
                    sendToPortfolioService(request, tenantId, userId);
                    count++;
                } catch (Exception e) {
                    log.error("Failed to process CSV row {}: {}", count + 1, e.getMessage());
                    throw new RuntimeException("Error on row " + (count + 1) + ": " + e.getMessage(), e);
                }
            }
            return count;
        } catch (Exception e) {
            log.error("Failed to parse CSV file: {}", e.getMessage());
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    private void sendToPortfolioService(InvestmentRequest request, String tenantId, Long userId) {
        String url = portfolioServiceUrl + "/api/v1/investments";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Tenant-Id", tenantId);
        headers.set("X-User-Id", userId.toString());
        // Simple internal trust — Gateway credentials not needed for service-to-service 
        // if they are on the same internal network, but we propagate identity.

        HttpEntity<InvestmentRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            restTemplate.postForEntity(url, entity, Object.class);
        } catch (Exception e) {
            log.error("Portfolio Service call failed: {}", e.getMessage());
            throw new RuntimeException("Portfolio Service error: " + e.getMessage());
        }
    }
}
