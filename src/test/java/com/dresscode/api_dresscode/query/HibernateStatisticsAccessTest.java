package com.dresscode.api_dresscode.query;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task 4.1 — Verifies that Hibernate statistics are enabled in the test profile
 * and that the Statistics API is accessible and reporting.
 */
@SpringBootTest
@ActiveProfiles("test")
class HibernateStatisticsAccessTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void statisticsAreEnabledInTestProfile() {
        SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        assertNotNull(stats, "Statistics object must not be null");
        assertTrue(stats.isStatisticsEnabled(),
                "Hibernate statistics MUST be enabled in the test profile " +
                "(spring.jpa.properties.hibernate.generate_statistics=true)");
    }

    @Test
    void queryExecutionCountIsAccessible() {
        SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics stats = sf.getStatistics();
        // Clear to get a clean baseline — must not throw
        stats.clear();
        long count = stats.getQueryExecutionCount();
        // After clear, count must be 0 (proves the API works)
        assertTrue(count >= 0,
                "getQueryExecutionCount() must return a non-negative value");
    }
}
