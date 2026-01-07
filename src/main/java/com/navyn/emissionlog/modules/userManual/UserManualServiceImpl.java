package com.navyn.emissionlog.modules.userManual;

import com.navyn.emissionlog.modules.userManual.exceptions.UserManualNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class UserManualServiceImpl implements UserManualService {

    private static final String STORAGE_PATH = "/app/user-manual";
    private static final String FILENAME = "user-manual.pdf";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    @Override
    public byte[] downloadUserManual() throws IOException {
        Path filePath = Paths.get(STORAGE_PATH, FILENAME);

        if (!Files.exists(filePath)) {
            throw new UserManualNotFoundException("User manual file not found");
        }

        return Files.readAllBytes(filePath);
    }

    @Override
    public void uploadUserManual(MultipartFile file) throws IOException {
        // Validate file
        validateFile(file);

        // Create directory if it doesn't exist with proper permissions
        Path storageDir = Paths.get(STORAGE_PATH);
        try {
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
                // Set permissions: rwxr-xr-x (755) - owner can read/write/execute, group and others can read/execute
                try {
                    Files.setPosixFilePermissions(
                        storageDir,
                        java.nio.file.attribute.PosixFilePermissions.fromString("rwxr-xr-x")
                    );
                } catch (UnsupportedOperationException e) {
                    // Windows or file system doesn't support POSIX permissions, skip
                    log.debug("POSIX permissions not supported, skipping permission setting");
                } catch (java.nio.file.AccessDeniedException e) {
                    // Permission setting failed, but directory was created - log and continue
                    log.warn("Could not set POSIX permissions on directory, but directory was created: {}", STORAGE_PATH);
                }
                log.info("Created user manual directory: {}", STORAGE_PATH);
            } else {
                // Ensure directory is writable
                if (!Files.isWritable(storageDir)) {
                    throw new IOException("Directory exists but is not writable: " + STORAGE_PATH);
                }
            }
        } catch (java.nio.file.AccessDeniedException e) {
            log.error("Access denied when creating directory: {}", STORAGE_PATH, e);
            throw new IOException("Cannot create or access user manual directory. Please ensure the application has write permissions to " + STORAGE_PATH, e);
        } catch (IOException e) {
            log.error("Error creating directory: {}", STORAGE_PATH, e);
            throw new IOException("Failed to create user manual directory: " + STORAGE_PATH + ". Error: " + e.getMessage(), e);
        }

        // Delete existing file if it exists
        Path filePath = Paths.get(STORAGE_PATH, FILENAME);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Deleted existing user manual file");
        }

        // Save new file
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("User manual uploaded successfully: {}", filePath);
        } catch (java.nio.file.AccessDeniedException e) {
            log.error("Access denied when writing file: {}", filePath, e);
            throw new IOException("Cannot write user manual file. Please ensure the application has write permissions to " + STORAGE_PATH, e);
        }
    }

    @Override
    public boolean userManualExists() {
        Path filePath = Paths.get(STORAGE_PATH, FILENAME);
        return Files.exists(filePath);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(PDF_CONTENT_TYPE)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        // Validate file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("File must have .pdf extension");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            long fileSizeMB = file.getSize() / (1024 * 1024);
            long maxSizeMB = MAX_FILE_SIZE / (1024 * 1024);
            throw new IllegalArgumentException(
                String.format("File size (%.2f MB) exceeds maximum allowed size of %d MB", 
                    fileSizeMB, maxSizeMB));
        }
    }
}

