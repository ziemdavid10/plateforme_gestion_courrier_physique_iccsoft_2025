package com.example.iccsoft_courrier.controller;

import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.services.CourrierServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = CourrierManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourrierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourrierServices courrierServices;
    
    @MockBean
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    
    @MockBean
    private com.example.iccsoft_courrier.services.CourrierStatusService statusService;
    
    @MockBean
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Courrier testCourrier;

    @BeforeEach
    void setUp() {
        testCourrier = new Courrier();
        testCourrier.setId(1L);
        testCourrier.setObjet("Test Courrier");
        testCourrier.setDestinateur("sender");
        testCourrier.setDestinataire("recipient");
        testCourrier.setDateCourrier(new Date());
        testCourrier.setDateEntree(new Date());
        testCourrier.setDescription("Test description");
        testCourrier.setEtat("En attente");
        testCourrier.setStatutCourrier("Public");
    }

    @Test
    void getAllCourriers_ShouldReturnCourriersList() throws Exception {
        when(jdbcTemplate.queryForList(any(String.class))).thenReturn(Arrays.asList());

        mockMvc.perform(get("/v1/api/secretary/courriers"))
                .andExpect(status().isOk());
    }

    @Test
    void createCourrier_WithSecretaireRole_ShouldCreateCourrier() throws Exception {
        when(jdbcTemplate.update(any(String.class), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(any(String.class), eq(Long.class), any(String.class))).thenReturn(1L);

        mockMvc.perform(post("/v1/api/secretary/courriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"objet\":\"Test\",\"destinataire\":\"test\",\"destinateur\":\"admin\",\"description\":\"test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void createCourrier_WithMissingData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/v1/api/secretary/courriers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourrier_ShouldUpdateStatus() throws Exception {
        when(jdbcTemplate.update(any(String.class), (Object[]) any())).thenReturn(1);
        when(courrierServices.getCourrierById(1L)).thenReturn(testCourrier);

        mockMvc.perform(put("/v1/api/secretary/courriers/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"etat\":\"TRAITE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCourrier_ShouldDeleteSuccessfully() throws Exception {
        when(courrierServices.deleteCourrier(1L)).thenReturn("Courrier supprimé");

        mockMvc.perform(delete("/v1/api/secretary/courriers/1"))
                .andExpect(status().isOk());
    }
}