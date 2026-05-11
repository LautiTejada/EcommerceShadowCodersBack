package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD RED: CORS must be configured via externalized property, never @CrossOrigin("*").
 *
 * Spec: Req — CORS Configuration — No Hardcoded or Wildcard Origins
 *   Scenario: Request from allowed origin → Access-Control-Allow-Origin present
 *   Scenario: Request from disallowed origin → header absent
 *   Scenario: No wildcard fallback
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String DISALLOWED_ORIGIN = "https://evil.example.com";

    @Test
    void optionsFromAllowedOrigin_returnsAccessControlHeader() throws Exception {
        MvcResult result = mockMvc.perform(options("/api/banners")
                .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andReturn();

        String allowOriginHeader = result.getResponse().getHeader("Access-Control-Allow-Origin");
        assertNotNull(allowOriginHeader,
                "Access-Control-Allow-Origin header must be present for allowed origin");
        assertEquals(ALLOWED_ORIGIN, allowOriginHeader,
                "Access-Control-Allow-Origin must echo the exact allowed origin, not wildcard");
    }

    @Test
    void optionsFromDisallowedOrigin_doesNotReturnAccessControlHeader() throws Exception {
        MvcResult result = mockMvc.perform(options("/api/banners")
                .header(HttpHeaders.ORIGIN, DISALLOWED_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andReturn();

        String allowOriginHeader = result.getResponse().getHeader("Access-Control-Allow-Origin");
        // For disallowed origins, the header should be absent or not match the disallowed origin
        if (allowOriginHeader != null) {
            assertNotEquals(DISALLOWED_ORIGIN, allowOriginHeader,
                    "Access-Control-Allow-Origin must NOT reflect a disallowed origin");
            assertNotEquals("*", allowOriginHeader,
                    "Access-Control-Allow-Origin must NOT be wildcard '*'");
        }
    }

    @Test
    void corsConfigurationSource_doesNotUseWildcard() throws Exception {
        MvcResult result = mockMvc.perform(options("/api/banners")
                .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andReturn();

        String allowOriginHeader = result.getResponse().getHeader("Access-Control-Allow-Origin");
        if (allowOriginHeader != null) {
            assertNotEquals("*", allowOriginHeader,
                    "CORS must never respond with wildcard '*' — use explicit origins");
        }
    }
}
