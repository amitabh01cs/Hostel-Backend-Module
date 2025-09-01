package com.example.testfrontendbackenddb.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class DriveProxyController {

    @GetMapping("/api/proxy-image")
    public void proxyImage(@RequestParam String url, HttpServletResponse response) {
        try {
            // Validate and open connection
            URL targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Check if image
            String contentType = connection.getContentType();
            if (contentType != null && contentType.startsWith("image")) {
                response.setContentType(contentType);
            } else {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            }

            // Copy stream
            try (InputStream in = connection.getInputStream()) {
                IOUtils.copy(in, response.getOutputStream());
            }

            response.flushBuffer();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
