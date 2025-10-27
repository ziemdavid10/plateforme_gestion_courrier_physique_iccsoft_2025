package com.example.iccsoft_courrier.services.impl;

import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.repositories.PieceJointeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PieceJointeServicesImplTest {

    @Mock
    private PieceJointeRepository pieceJointeRepository;

    @InjectMocks
    private PieceJointeServicesImpl pieceJointeServices;

    private PieceJointe pieceJointe;

    @BeforeEach
    void setUp() {
        pieceJointe = new PieceJointe();
        pieceJointe.setId(1L);
        pieceJointe.setCourrierId(1L);
        pieceJointe.setFileName("test.pdf");
        pieceJointe.setFilePath("/uploads/test.pdf");
        pieceJointe.setFileSize(1024L);
        pieceJointe.setContentType("application/pdf");
    }

    @Test
    void createPieceJointe_ShouldCreateSuccessfully() {
        when(pieceJointeRepository.save(any(PieceJointe.class))).thenReturn(pieceJointe);

        PieceJointe result = pieceJointeServices.createPieceJointe(pieceJointe);

        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
        verify(pieceJointeRepository).save(pieceJointe);
    }

    @Test
    void createPieceJointe_ShouldThrowExceptionWhenNoCourrierId() {
        pieceJointe.setCourrierId(null);

        assertThrows(RuntimeException.class, () -> 
            pieceJointeServices.createPieceJointe(pieceJointe));
    }

    @Test
    void getPieceJointeById_ShouldReturnPieceJointe() {
        when(pieceJointeRepository.findById(1L)).thenReturn(Optional.of(pieceJointe));

        PieceJointe result = pieceJointeServices.getPieceJointeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPieceJointesByCourrierId_ShouldReturnList() {
        when(pieceJointeRepository.findByCourrierId(1L)).thenReturn(List.of(pieceJointe));

        List<PieceJointe> result = pieceJointeServices.getPieceJointesByCourrierId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test.pdf", result.get(0).getFileName());
    }

    @Test
    void deletePieceJointe_ShouldDeleteSuccessfully() {
        when(pieceJointeRepository.findById(1L)).thenReturn(Optional.of(pieceJointe));

        String result = pieceJointeServices.deletePieceJointe(1L);

        assertEquals("PieceJointe with id: 1 deleted successfully", result);
        verify(pieceJointeRepository).delete(pieceJointe);
    }
}