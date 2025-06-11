package app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Jwt {
    private static final String SECRET_KEY = "YourSuperSecretKey";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final int EXPIRATION_TIME = 86400; // 24h

    // Generate JWT Token with multiple payload fields
    public static String generateToken(Map<String, Object> payloadData) {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        StringBuilder payloadBuilder = new StringBuilder("{");
        payloadData.forEach((key, value) -> {
            payloadBuilder.append("\"").append(key).append("\":");
            if (value instanceof Number) {
                payloadBuilder.append(value).append(",");
            } else {
                payloadBuilder.append("\"").append(value).append("\",");
            }
        });
        payloadBuilder.append("\"exp\":").append(System.currentTimeMillis() / 1000 + EXPIRATION_TIME).append("}");

        String encodedHeader = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadBuilder.toString().getBytes(StandardCharsets.UTF_8));

        String signature = hmacSha256(encodedHeader + "." + encodedPayload);
        return encodedHeader + "." + encodedPayload + "." + signature;
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

    private static String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }

    private static String hmacSha256(String data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(Jwt.SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            return base64UrlEncode(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error signing JWT", e);
        }
    }

    public static String getEmailFromToken(String token) throws JsonProcessingException {
        String jsonPayload = getPayload(token);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);
        return jsonNode.get("email").asText();
    }

    public static String getUidFromToken(String token) throws JsonProcessingException {
        String jsonPayload = getPayload(token);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);
        return jsonNode.get("uid").asText();


    }
}
