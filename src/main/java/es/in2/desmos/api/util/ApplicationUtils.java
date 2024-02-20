package es.in2.desmos.api.util;

import es.in2.desmos.api.exception.HashLinkException;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ApplicationUtils {

    public static final String SHA_256_ALGORITHM = "SHA-256";

    public static final String HASH_PREFIX = "0x";

    public static final String HASHLINK_PREFIX = "?hl=";

    private ApplicationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String calculateSHA256Hash(String data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(SHA_256_ALGORITHM);
        byte[] hash = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
    public static String calculateIntertwinedHash(byte[] hash1, byte[] hash2) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_256_ALGORITHM);
        digest.update(hash1);
        digest.update(hash2);
        byte[] result = digest.digest();
        return bytesAHex(result);
    }

    private static String bytesAHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String extractEntityHashFromDataLocation(String dataLocation) {
        return Arrays.stream(dataLocation.split("\\?hl="))
                .skip(1)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static String extractEntityIdFromDataLocation(String dataLocation) {
        Pattern pattern = Pattern.compile("entities/(.*?)\\?hl=");
        Matcher matcher = pattern.matcher(dataLocation);
        return Optional.of(matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1))
                .orElseThrow(() -> new IllegalArgumentException("Invalid data location format"));
    }

    public static String extractEntityUrlFromDataLocation(String dataLocation) {
        return Arrays.stream(dataLocation.split("\\?hl="))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static boolean hasHlParameter(String urlString) {
        try {
            URL url = new URL(urlString);
            Map<String, String> queryParams = splitQuery(url);
            log.debug("Query params: {}", queryParams);
            if (queryParams.containsKey("hl")) {
                log.debug("Query param hl: {}", queryParams.get("hl"));
                return queryParams.containsKey("hl");
            } else {
                throw new HashLinkException("Query param hl not found");
            }
        } catch (MalformedURLException e) {
            throw new HashLinkException("Error parsing dataLocation");
        }
    }

    private static Map<String, String> splitQuery(URL url) {
        if (url.getQuery() == null || url.getQuery().isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> queryPairs = new HashMap<>();
        String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(pair.substring(0, idx), idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null);
        }
        return queryPairs;
    }

}
