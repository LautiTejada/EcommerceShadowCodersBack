package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD TRIANGULATE: Unit tests for the parseOrigins pure function.
 * No Spring context needed — pure logic test.
 *
 * Spec: Req — CORS Configuration — No Hardcoded or Wildcard Origins
 *   Scenario: No wildcard fallback when empty
 */
class CorsConfigParseOriginsTest {

    @Test
    void parseOrigins_singleOrigin_returnsSingleEntry() {
        List<String> result = CorsConfig.parseOrigins("https://app.example.com");
        assertEquals(1, result.size());
        assertEquals("https://app.example.com", result.get(0));
    }

    @Test
    void parseOrigins_multipleOrigins_returnsAllEntries() {
        List<String> result = CorsConfig.parseOrigins("https://app.example.com,https://admin.example.com");
        assertEquals(2, result.size());
        assertTrue(result.contains("https://app.example.com"));
        assertTrue(result.contains("https://admin.example.com"));
    }

    @Test
    void parseOrigins_blank_returnsEmptyList() {
        List<String> result = CorsConfig.parseOrigins("   ");
        assertEquals(0, result.size(),
                "Blank input must produce empty list — no wildcard fallback");
    }

    @Test
    void parseOrigins_null_returnsEmptyList() {
        List<String> result = CorsConfig.parseOrigins(null);
        assertEquals(0, result.size(),
                "Null input must produce empty list — no wildcard fallback");
    }

    @Test
    void parseOrigins_withExtraSpaces_trimsEachEntry() {
        List<String> result = CorsConfig.parseOrigins(" https://app.example.com , https://admin.example.com ");
        assertEquals(2, result.size());
        assertTrue(result.contains("https://app.example.com"));
        assertTrue(result.contains("https://admin.example.com"));
    }
}
