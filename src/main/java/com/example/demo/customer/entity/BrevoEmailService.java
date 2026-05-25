package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendEmail(String toEmail, String toName, String subject, String htmlContent) {
        try {
            String body = """
                {
                  "sender": { "email": "%s", "name": "%s" },
                  "to": [{ "email": "%s", "name": "%s" }],
                  "subject": "%s",
                  "htmlContent": "%s"
                }
                """.formatted(
                    senderEmail, senderName,
                    toEmail, toName,
                    subject,
                    htmlContent.replace("\"", "\\\"").replace("\n", "")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("✅ Email sent to " + toEmail);
            } else {
                System.err.println("❌ Brevo error " + response.statusCode() + ": " + response.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}