package com.example.iccsoft_courrier.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourrierTest {

    private Courrier courrier;

    @BeforeEach
    void setUp() {
        courrier = new Courrier();
    }

    @Test
    void genererNumeroOrdre_ShouldGenerateValidFormat() {
        String numeroOrdre = courrier.genererNumeroOrdre();

        assertNotNull(numeroOrdre);
        assertTrue(numeroOrdre.startsWith("CE-"));
        assertTrue(numeroOrdre.matches("CE-\\d{8}-\\d{3}"));
    }

    @Test
    void onCreate_ShouldSetDates() {
        courrier.onCreate();

        assertNotNull(courrier.getDateReception());
        assertNotNull(courrier.getDateModification());
    }

    @Test
    void courrier_ShouldSetAndGetProperties() {
        courrier.setObjet("Test Objet");
        courrier.setDestinataire("test@example.com");
        courrier.setDestinateur("Admin");
        courrier.setDescription("Test description");
        courrier.setEtat("RECU");
        courrier.setStatutCourrier("PUBLIC");

        assertEquals("Test Objet", courrier.getObjet());
        assertEquals("test@example.com", courrier.getDestinataire());
        assertEquals("Admin", courrier.getDestinateur());
        assertEquals("Test description", courrier.getDescription());
        assertEquals("RECU", courrier.getEtat());
        assertEquals("PUBLIC", courrier.getStatutCourrier());
    }
}