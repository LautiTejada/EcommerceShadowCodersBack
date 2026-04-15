package com.dresscode.api_dresscode.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponseDTO {
    private boolean success;
    private String filename;
    private String path;
    private long size;
    private String message;
}
