package com.example.iccsoft_courrier.integration;

import com.example.iccsoft_courrier.models.Courrier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class CourrierIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void testCompleteCourrierWorkflow() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Courrier courrier = new Courrier();
        courrier.setObjet("Test Integration");
        courrier.setDestinateur("secretaire.test");
        courrier.setDestinataire("test.user");
        courrier.setDateCourrier(new Date());
        courrier.setDateEntree(new Date());
        courrier.setDescription("Test d'intégration complète");
        courrier.setEtat("En attente");
        courrier.setStatutCourrier("Public");

        // Test création avec rôle SECRETAIRE
        mockMvc.perform(post("/v3/courrier")
                .param("employeId", "1")
                .header("X-User-Role", "SECRETAIRE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courrier)))
                .andExpect(status().isCreated());

        // Test accès refusé avec rôle EMPLOYE
        mockMvc.perform(post("/v3/courrier")
                .param("employeId", "1")
                .header("X-User-Role", "EMPLOYE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courrier)))
                .andExpect(status().isForbidden());
    }
}