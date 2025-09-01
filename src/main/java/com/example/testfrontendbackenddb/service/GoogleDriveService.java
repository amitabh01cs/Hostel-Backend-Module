package com.example.testfrontendbackenddb.service;

import com.example.testfrontendbackenddb.controller.GoogleOAuthController;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "HostelRegistrationApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleOAuthController googleOAuthController;

    public GoogleDriveService(GoogleOAuthController googleOAuthController) {
        this.googleOAuthController = googleOAuthController;
    }

    /**
     * Uploads a file to Google Drive user's My Drive and returns direct preview link.
     */
    public String uploadFile(java.io.File filePath, String mimeType)
            throws IOException, GeneralSecurityException {

        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // Step 1 — Load stored OAuth user credentials
        Credential credential;
        try {
            credential = googleOAuthController.getCredential();
            if (credential == null) {
                throw new RuntimeException("Google Drive not connected. Login via /google/login first.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Google OAuth credentials", e);
        }

        // Step 2 — Build Drive service
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Step 3 — File metadata
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());

        // Step 4 — File content
        FileContent mediaContent = new FileContent(mimeType, filePath);

        // Step 5 — Upload
        File uploadedFile = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        String fileId = uploadedFile.getId();

        // -------- OPTIONAL --------
        // Step 6 — Make file public: "Anyone with the link can view"
        Permission publicPermission = new Permission();
        publicPermission.setType("anyone");
        publicPermission.setRole("reader");
        service.permissions().create(fileId, publicPermission)
                .setFields("id")
                .execute();
        // -------------------------

        // Step 7 — Return direct preview link
        // This link is public and can be shown directly on the frontend
        return "https://drive.google.com/uc?id=" + fileId;
    }
}
