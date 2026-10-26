package com.auction.virtual_auction_system.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates and validates compact HMAC-SHA256 JWTs without an additional
 * runtime dependency. Set {@code app.jwt.secret} to a strong secret in every
 * deployed environment.
 */
@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final String DEFAULT_SECRET =
            "change-this-development-secret-change-this-development-secret";
    private static final long DEFAULT_EXPIRATION_MILLIS = 86_400_000L;

    private static final Pattern SUBJECT_PATTERN = Pattern.compile(
            "\\\"sub\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\"");
    private static final Pattern EXPIRATION_PATTERN = Pattern.compile(
            "\\\"exp\\\"\\s*:\\s*(\\d+)");

    private final SecretKeySpec signingKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret:" + DEFAULT_SECRET + "}") String secret,
            @Value("${app.jwt.expiration-ms:" + DEFAULT_EXPIRATION_MILLIS + "}")
            long expirationMillis) {

        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 UTF-8 bytes"
            );
        }

        if (expirationMillis <= 0) {
            throw new IllegalArgumentException(
                    "JWT expiration must be greater than zero"
            );
        }

        this.signingKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );
        this.expirationMillis = expirationMillis;
    }

    /**
     * Creates a token whose subject is the user's username/email.
     */
    public String generateToken(UserDetails userDetails) {

        Objects.requireNonNull(userDetails, "userDetails must not be null");

        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = Instant.now()
                .plusMillis(expirationMillis)
                .getEpochSecond();

        String encodedHeader = encode(JWT_HEADER);
        String payload = "{\"sub\":\""
                + escapeJson(userDetails.getUsername())
                + "\",\"iat\":"
                + issuedAt
                + ",\"exp\":"
                + expiresAt
                + "}";
        String encodedPayload = encode(payload);
        String signingInput = encodedHeader + "." + encodedPayload;

        return signingInput + "." + encode(sign(signingInput));
    }

    /**
     * Extracts and verifies the username from a token.
     *
     * @throws IllegalArgumentException when the token is malformed, expired,
     *                                  or has an invalid signature
     */
    public String extractUsername(String jwt) {

        return parseAndVerify(jwt).subject();
    }

    /**
     * Returns true only when the token is valid, unexpired, and belongs to
     * the supplied user.
     */
    public boolean isTokenValid(String jwt, UserDetails userDetails) {

        if (userDetails == null) {
            return false;
        }

        try {
            TokenPayload payload = parseAndVerify(jwt);
            return payload.expiresAt() > Instant.now().getEpochSecond()
                    && userDetails.getUsername().equals(payload.subject());
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private TokenPayload parseAndVerify(String jwt) {

        if (jwt == null || jwt.isBlank()) {
            throw new IllegalArgumentException("JWT must not be blank");
        }

        String[] parts = jwt.split("\\.", -1);
        if (parts.length != 3 || parts[0].isBlank()
                || parts[1].isBlank() || parts[2].isBlank()) {
            throw new IllegalArgumentException("Malformed JWT");
        }

        String header = decode(parts[0]);
        if (!JWT_HEADER.equals(header)) {
            throw new IllegalArgumentException("Unsupported JWT header");
        }

        byte[] actualSignature = decodeBytes(parts[2]);
        byte[] expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!MessageDigest.isEqual(actualSignature, expectedSignature)) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }

        String payload = decode(parts[1]);
        Matcher subjectMatcher = SUBJECT_PATTERN.matcher(payload);
        Matcher expirationMatcher = EXPIRATION_PATTERN.matcher(payload);
        if (!subjectMatcher.find() || !expirationMatcher.find()) {
            throw new IllegalArgumentException("JWT is missing required claims");
        }

        try {
            long expiresAt = Long.parseLong(expirationMatcher.group(1));
            if (expiresAt <= Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("JWT has expired");
            }
            return new TokenPayload(
                    unescapeJson(subjectMatcher.group(1)),
                    expiresAt
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid JWT expiration", exception);
        }
    }

    private byte[] sign(String value) {

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            return mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }

    private static String encode(String value) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(byte[] value) {

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value);
    }

    private static String decode(String value) {

        return new String(decodeBytes(value), StandardCharsets.UTF_8);
    }

    private static byte[] decodeBytes(String value) {

        try {
            return Base64.getUrlDecoder().decode(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid base64url JWT part", exception);
        }
    }

    private static String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String value) {

        StringBuilder result = new StringBuilder(value.length());
        boolean escaped = false;

        for (char character : value.toCharArray()) {
            if (escaped) {
                switch (character) {
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    default -> throw new IllegalArgumentException(
                            "Invalid JSON escape in JWT subject"
                    );
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else {
                result.append(character);
            }
        }

        if (escaped) {
            throw new IllegalArgumentException("Invalid JSON escape in JWT subject");
        }

        return result.toString();
    }

    private record TokenPayload(String subject, long expiresAt) {
    }
}
