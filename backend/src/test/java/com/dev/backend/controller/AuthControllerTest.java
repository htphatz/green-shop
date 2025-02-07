package com.dev.backend.controller;

import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final static String END_POINT = "auth";
    private RegisterReq registerReq;
    private UserRes userRes;

    @BeforeEach
    public void initData() {
        registerReq = RegisterReq.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .build();

        userRes = UserRes.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .build();
    }

    @Test
    public void register_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerReq);

        Mockito.when(authService.register(ArgumentMatchers.any())).thenReturn(userRes);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post(END_POINT + "/register")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("firstName").value("test"));
    }
}
