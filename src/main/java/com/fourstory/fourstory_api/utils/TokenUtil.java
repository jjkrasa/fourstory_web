package com.fourstory.fourstory_api.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TokenUtil {

    public String generateRaw() {
        byte[] bytes = new byte[32];
        ThreadLocalRandom.current().nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public byte[] sha256(String raw) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.US_ASCII));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Duration hours(int h) {
        return Duration.ofHours(h);
    }
}
