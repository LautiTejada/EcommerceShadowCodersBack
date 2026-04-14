package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.DTOs.UploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    @Value("${file.upload.dir:public/assets/banners/}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));

    @PostMapping("/banner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UploadResponseDTO> uploadBanner(@RequestParam("file") MultipartFile file) {
        try {
            validateFile(file);
            String filename = generateFileName(file);
            createDirectoryIfNotExists();
            
            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, file.getBytes());

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    UploadResponseDTO.builder()
                            .success(true)
                            .filename(filename)
                            .path("/assets/banners/" + filename)
                            .size(file.getSize())
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    UploadResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    UploadResponseDTO.builder()
                            .success(false)
                            .message("Error al guardar: " + e.getMessage())
                            .build()
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede 5MB");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Extensión inválida");
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Formato no permitido. Use: jpg, jpeg, png, webp");
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        return "banner_" + UUID.randomUUID().toString() + extension;
    }

    private void createDirectoryIfNotExists() throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
    }
}
