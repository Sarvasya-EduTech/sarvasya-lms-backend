package com.sarvasya.sarvasya_lms_backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityInvalidTokenTest {

    private final MockMvc mockMvc;

    SecurityInvalidTokenTest(@Autowired WebApplicationContext wac) {
        this.mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    void protectedEndpointWithoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/sarvasya/tenants"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidBearerToken_isRejectedWithJsonError() throws Exception {
        mockMvc.perform(get("/sarvasya/tenants")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":401")));
    }
}


