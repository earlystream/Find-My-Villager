package com.earlystream.tradecompass.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

public final class SafeFileNameUtil {
    private SafeFileNameUtil() {
    }

    public static String safeKey(String prefix, String rawIdentity) {
        String raw = rawIdentity == null || rawIdentity.isBlank() ? "unknown" : rawIdentity;
        String sanitizedPrefix = sanitize(prefix == null ? "world" : prefix);
        return sanitizedPrefix + "-" + sha256(raw).substring(0, 16);
    }

    private static String sanitize(String value) {
        String sanitized = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "-");
        return sanitized.isBlank() ? "world" : sanitized;
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            return Integer.toHexString(value.hashCode());
        }
    }
}
