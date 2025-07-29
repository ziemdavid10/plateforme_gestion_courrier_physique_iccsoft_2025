package com.example.iccsoft_courrier.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.iccsoft_courrier.exception.CourrierExceptions;
import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.services.CourrierServices;
import com.example.iccsoft_courrier.services.FileStorageService;

import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.MediaType;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/v3/courrier")
public class CourrierController {

    private final CourrierServices courrierServices;
    private final FileStorageService fileStorageService;

    public static final Logger LOGGER = Logger.getLogger(CourrierController.class.getName());

    public CourrierController(CourrierServices courrierServices, FileStorageService fileStorageService) {
        this.courrierServices = courrierServices;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity <List<Courrier>> getAll() {
        return new ResponseEntity<>(courrierServices.all(), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public boolean uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileStorageService.saveFile(file);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File upload failed", e);
            return false;
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("file") String fileName){
        try {
            var fileToDownload = fileStorageService.downloadFile(fileName);
            return ResponseEntity.ok()
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileToDownload.getName() + "\"")
                    .body(new FileSystemResource(fileToDownload));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error downloading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Courrier> createCourrier(@RequestBody Courrier courrier, @RequestParam Long employeId) {
        return new ResponseEntity<>(courrierServices.createCourrier(courrier, employeId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Courrier> updateCourrier(@RequestBody Courrier courrier, @PathVariable Long id) {
        Courrier updatedCourrier = courrierServices.updateCourrier(courrier, id);
        if (updatedCourrier != null){
            return new ResponseEntity<>(updatedCourrier, HttpStatus.OK);
        } else {
            throw new CourrierExceptions("Employe not found");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Courrier> getCourrierById(@PathVariable Long id) {
        return new ResponseEntity<>(courrierServices.getCourrierById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourrier(@PathVariable Long id) {
        String response = courrierServices.deleteCourrier(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
}
