package com.example.iccsoft_courrier.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceJointeTest {

    private PieceJointe pieceJointe;

    @BeforeEach
    void setUp() {
        pieceJointe = new PieceJointe();
    }

    @Test
    void pieceJointe_ShouldSetAndGetProperties() {
        pieceJointe.setId(1L);
        pieceJointe.setCourrierId(100L);
        pieceJointe.setFileName("document.pdf");
        pieceJointe.setFilePath("/uploads/document.pdf");
        pieceJointe.setFileSize(2048L);
        pieceJointe.setContentType("application/pdf");

        assertEquals(1L, pieceJointe.getId());
        assertEquals(100L, pieceJointe.getCourrierId());
        assertEquals("document.pdf", pieceJointe.getFileName());
        assertEquals("/uploads/document.pdf", pieceJointe.getFilePath());
        assertEquals(2048L, pieceJointe.getFileSize());
        assertEquals("application/pdf", pieceJointe.getContentType());
    }

    @Test
    void pieceJointe_ShouldHandleNullValues() {
        assertNull(pieceJointe.getId());
        assertNull(pieceJointe.getCourrierId());
        assertNull(pieceJointe.getFileName());
        assertNull(pieceJointe.getFilePath());
        assertNull(pieceJointe.getFileSize());
        assertNull(pieceJointe.getContentType());
    }
}