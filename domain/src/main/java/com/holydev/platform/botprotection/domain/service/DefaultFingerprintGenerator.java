package com.holydev.platform.botprotection.domain.service;

import com.holydev.platform.botprotection.domain.model.Fingerprint;
import com.holydev.platform.botprotection.domain.model.RequestContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class DefaultFingerprintGenerator implements FingerprintGenerator {

    @Override
    public Fingerprint generate(RequestContext context) {

        String raw = String.join("|",
                safe(context.getIp()),
                safe(context.getUserAgent()),
                safe(context.getHeaders().get("Accept-Language")),
                safe(context.getHeaders().get("Accept-Encoding"))
        );

        return Fingerprint.builder()
                .hash(sha256(raw))
                .build();
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}