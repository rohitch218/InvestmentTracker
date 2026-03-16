package com.investtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point of the Investment Tracker application.
 *
 * @EnableJpaAuditing — activates @CreatedDate / @LastModifiedDate
 * auto-population on all entities that extend BaseEntity.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class InvestmentTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentTrackerApplication.class, args);
    }
}
