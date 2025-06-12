package app.utils;

import app.dto.UserDetailsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Jwt {
    private static final String SECRET_KEY = "YourSuperSecretKey";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int EXPIRATION_TIME = 86400; // 24h

    // Generate JWT Token using UserDetailsDTO
    public static String generateToken(UserDetailsDTO userDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(userDetails);

            // Add expiration time
            String finalPayload = payload.substring(0, payload.length() - 1) +
                    ",\"exp\":" + (System.currentTimeMillis() / 1000 + EXPIRATION_TIME) + "}";

            String encodedHeader = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
            String encodedPayload = base64UrlEncode(finalPayload.getBytes());

            String signature = hmacSha256(encodedHeader + "." + encodedPayload);
            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    // Validate Token
    public static boolean validateToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return false;

        String expectedSignature = hmacSha256(parts[0] + "." + parts[1]);
        return expectedSignature.equals(parts[2]);
    }

    // Decode Payload
    public static String getPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;

        return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
    }

    // Extract UserDetailsDTO from Token
    public static UserDetailsDTO getUserDetailsFromToken(String token) {
        try {
            String payload = getPayload(token);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload, UserDetailsDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    private static String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }

    private static String hmacSha256(String data) {
        try {
            Mac sha256Hmac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            sha256Hmac.init(secretKey);
            return base64UrlEncode(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error signing JWT", e);
        }
    }

    public static String getEmailFromToken(String token) {
        try {
            String payload = getPayload(token);
            ObjectMapper objectMapper = new ObjectMapper();
            UserDetailsDTO userDetails = objectMapper.readValue(payload, UserDetailsDTO.class);
            return userDetails.getEmail();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }
}
