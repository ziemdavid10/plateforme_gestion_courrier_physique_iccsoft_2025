package com.example.iccsoft_courrier.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.iccsoft_courrier.services.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    public static final String FILE_STORAGE_PATH = "../../../iccsoft_courrier/Courrier_Telecharge"; // lieu de stockage
                                                                                                    // des fichiers

    @Override
    public void saveFile(MultipartFile file) throws IOException {

        if (file == null) {
            throw new NullPointerException("File cannot be null");
        }
        File targetFile = new File(FILE_STORAGE_PATH + File.separator + file.getOriginalFilename());
        if (!Objects.equals(targetFile.getParent(), FILE_STORAGE_PATH)) {
            throw new SecurityException("Cannot save file outside of designated storage path");
        }
        Files.copy(file.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public File downloadFile(String fileName) throws FileNotFoundException {
        if (fileName == null || fileName.isEmpty()) {
            throw new NullPointerException("File name cannot be null or empty");
        }
        File fileToDownload = new File(FILE_STORAGE_PATH + File.separator + fileName);
        if (!Objects.equals(fileToDownload.getParent(), FILE_STORAGE_PATH)) {
            throw new SecurityException("Cannot save file outside of designated storage path");
        }
        if (!fileToDownload.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return fileToDownload; // retourne le fichier pour téléchargement
    }

    @Override
    public void deleteFile(String fileName) throws FileNotFoundException {
        if (fileName == null || fileName.isEmpty()) {
            throw new NullPointerException("File name cannot be null or empty");
        }
        File fileToDelete = new File(FILE_STORAGE_PATH + File.separator + fileName);
        if (!Objects.equals(fileToDelete.getParent(), FILE_STORAGE_PATH)) {
            throw new SecurityException("Cannot delete file outside of designated storage path");
        }
        if (fileToDelete.exists()) {
            if (!fileToDelete.delete()) {
                throw new RuntimeException("Failed to delete file: " + fileName);
            }
        } else {
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }
}
