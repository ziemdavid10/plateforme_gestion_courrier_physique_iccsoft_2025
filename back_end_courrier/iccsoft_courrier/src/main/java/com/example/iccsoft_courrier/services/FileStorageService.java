package com.example.iccsoft_courrier.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Saves a file to the storage.
     *
     * @param MultipartFile the file to save
     */
    void saveFile(MultipartFile file
    // , byte[] content
    ) throws IOException;

    // /**
    //  * Loads a file from the storage.
    //  *
    //  * @param fileName the name of the file to load
    //  * @return the content of the file as a byte array
    //  */
    File downloadFile(String fileName) throws FileNotFoundException;

    // /**
    //  * Deletes a file from the storage.
    //  *
    //  * @param fileName the name of the file to delete
    //  */
    void deleteFile(String fileName) throws FileNotFoundException;
    
}
