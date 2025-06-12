package app.utils;

import app.dto.UserDetailsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.core.type.TypeReference;

public class Jwt {
    private static final String SECRET_KEY = "YourSuperSecretKey";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int EXPIRATION_TIME = 86400; // 24h

    // Generate JWT Token
    public static String generateToken(UserDetailsDTO userDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(userDetails);

            // Add expiration timestamp
            String finalPayload = payload.substring(0, payload.length() - 1) +
                    ",\"exp\":" + (System.currentTimeMillis() / 1000 + EXPIRATION_TIME) + "}";

            String encodedHeader = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
            String encodedPayload = base64UrlEncode(finalPayload.getBytes());

            String signature = hmacSha256(encodedHeader + "." + encodedPayload);
            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception e) {
            System.out.println("Error generating token: " + e.getMessage());
            throw new RuntimeException("Error generating token", e);
        }
    }

    // Validate Token
    public static boolean validateToken(String token) {
        System.out.println("Validating token: " + token);

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            System.out.println("Invalid token format: " + token);
            return false;
        }

        String expectedSignature = hmacSha256(parts[0] + "." + parts[1]);
        boolean isValid = expectedSignature.equals(parts[2]);

        System.out.println("Expected Signature: " + expectedSignature);
        System.out.println("Provided Signature: " + parts[2]);
        System.out.println("Token Validation Result: " + isValid);

        return isValid;
    }

    // Decode Payload
    public static String getPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            System.out.println("Invalid JWT structure: " + token);
            return null;
        }

        String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        System.out.println("Decoded Payload: " + decodedPayload);
        return decodedPayload;
    }

    // Extract UserDetailsDTO from Token
    public static UserDetailsDTO getUserDetailsFromToken(String token) {
        try {
            String payload = getPayload(token);
            System.out.println("Extracted Payload: " + payload);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> parsedPayload = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});

            String email = (String) parsedPayload.get("email");
            String uid = (String) parsedPayload.get("uid");
            String role = (String) parsedPayload.get("role");

            UserDetailsDTO userDetails = new UserDetailsDTO(email, uid, role);
            System.out.println("Parsed UserDetailsDTO: " + userDetails);
            return userDetails;
        } catch (Exception e) {
            System.out.println("Error parsing token: " + e.getMessage());
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
            System.out.println("Error signing JWT: " + e.getMessage());
            throw new RuntimeException("Error signing JWT", e);
        }
    }

    public static String getEmailFromToken(String token) {
        try {
            String payload = getPayload(token);
            System.out.println("Extracted Payload for Email: " + payload);

            ObjectMapper objectMapper = new ObjectMapper();
            UserDetailsDTO userDetails = objectMapper.readValue(payload, UserDetailsDTO.class);
            System.out.println("Extracted Email: " + userDetails.getEmail());
            return userDetails.getEmail();
        } catch (Exception e) {
            System.out.println("Error parsing token for email: " + e.getMessage());
            throw new RuntimeException("Error parsing token for email", e);
        }
    }
}
