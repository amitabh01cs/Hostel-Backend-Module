package com.example.testfrontendbackenddb.controller;

import com.example.testfrontendbackenddb.service.MySQLDataStoreFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

@Controller
public class GoogleOAuthController {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json"; // apni JSON file ka path
    private static final String USER_ID = "mydrive-user"; // Aap isse dynamically bhi manage kar sakte hain multi user ke liye

    private final MySQLDataStoreFactory mySQLDataStoreFactory;

    public GoogleOAuthController(MySQLDataStoreFactory mySQLDataStoreFactory) {
        this.mySQLDataStoreFactory = mySQLDataStoreFactory;
    }

    /** Step 3.1: Redirect user to Google consent screen */
    @GetMapping("/google/login")
    public void googleLogin(HttpServletResponse response) throws Exception {
        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new RuntimeException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singletonList(DriveScopes.DRIVE_FILE))  // Drive file scope
                .setDataStoreFactory(mySQLDataStoreFactory)          // Token MySQL me save hoga
                .setAccessType("offline")                             // Refresh token milne ke liye
                .build();

        // Google OAuth consent URL build kar rahe hain
        String redirectUrl = flow.newAuthorizationUrl()
                .setRedirectUri("https://backend-hostel-module-production.up.railway.app/oauth2/callback") // aapke callback URI ke hisab se
                .build() + "&prompt=consent"; // Manual prompt add kiya hai taaki refresh token mile

        response.sendRedirect(redirectUrl);
    }

    /** Step 3.2: Callback endpoint to handle Google response, exchange code for tokens */
    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam("code") String code) throws Exception {
        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new RuntimeException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singletonList(DriveScopes.DRIVE_FILE))
                .setDataStoreFactory(mySQLDataStoreFactory)
                .setAccessType("offline")
                .build();

        // Authorization code ko access token me convert kar rahe hain
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri("https://backend-hostel-module-production.up.railway.app/oauth2/callback")
                .execute();

        // Token DB me save kar rahe hain USER_ID key se (multi-user ke liye dynamic ID use karein)
        flow.createAndStoreCredential(tokenResponse, USER_ID);

        return "Google Drive connected successfully!";
    }

    /** Step 3.3: Get stored Credential to use in Drive API calls */
    public Credential getCredential() throws Exception {
        var HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new RuntimeException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singletonList(DriveScopes.DRIVE_FILE))
                .setDataStoreFactory(mySQLDataStoreFactory)
                .setAccessType("offline")
                .build();

        // Stored token retrieve karte hain (file upload ke liye)
        return flow.loadCredential(USER_ID);
    }
}
