package com.investtracker.tenant.service;

import com.investtracker.tenant.entity.Tenant;
import com.investtracker.tenant.exception.DuplicateResourceException;
import com.investtracker.tenant.exception.ResourceNotFoundException;
import com.investtracker.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant createTenant(String name) {
        if (tenantRepository.existsByName(name)) {
            throw new DuplicateResourceException("Tenant with name '" + name + "' already exists");
        }

        String tenantId = UUID.randomUUID().toString();
        Tenant tenant = Tenant.builder()
            .id(tenantId)
            .name(name)
            .plan(Tenant.TenantPlan.FREE)
            .isActive(true)
            .build();

        log.info("Creating new tenant: {} with ID: {}", name, tenantId);
        return tenantRepository.save(tenant);
    }

    public Tenant getTenantById(String id) {
        return tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + id));
    }
}
