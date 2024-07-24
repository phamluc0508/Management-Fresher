package com.vmo.management_fresher.api;

import jakarta.persistence.EntityExistsException;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;
import com.vmo.management_fresher.service.EmployeeService;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeReq request;
    private EmployeeRes response;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(new AuthenticationReq()
                .builder()
                .username("admin")
                .password("admin")
                .build());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content));
        MvcResult mvcResult = resultActions.andDo(MockMvcResultHandlers.print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);
        token = "Bearer " + jsonObject.getJSONObject("data").getString("token");
    }

    @BeforeEach
    void initData() {
        request = EmployeeReq.builder()
                .firstName("John")
                .middleName("Doe")
                .lastName("Smith")
                .email("test@gmail.com")
                .phoneNumber("0123456789")
                .build();

        response = EmployeeRes.builder()
                .id(1L)
                .firstName("John")
                .middleName("Doe")
                .lastName("Smith")
                .email("test@gmail.com")
                .phoneNumber("0123456789")
                .accountId("axbycz147")
                .username("test@gmail.com")
                .build();
    }

    @Test
    void createEmployee_Test_Success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(employeeService.createEmployee(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/employee")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.email").value("test@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.username").value("test@gmail.com"));
    }

    @Test
    void createEmployee_Test_Fail() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(employeeService.createEmployee(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenThrow(new EntityExistsException("email-used"));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/employee")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("409"))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("email-used"));
    }
}
