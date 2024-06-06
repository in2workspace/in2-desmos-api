package es.in2.desmos.domain.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ApplicationUtils {

    private ApplicationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String calculateSHA256(String data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(result);
    }

    public static String calculateHashLink(String previousHash, String entityHash) throws NoSuchAlgorithmException {
        String hashConcatenated = previousHash + entityHash;
        log.debug("Previous Hash 1 : {}", previousHash);
        log.debug("Entity Hash 2 : {}", entityHash);
        log.debug("Hash Concatenated : {}", hashConcatenated);
        return calculateSHA256(hashConcatenated);
    }

    public static String extractHashLinkFromDataLocation(String dataLocation) {
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

    public static String extractContextBrokerUrlFromDataLocation(String dataLocation) {
        return Arrays.stream(dataLocation.split("\\?hl="))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static String getEnvironmentMetadata(String activeProfile) {
        return switch (activeProfile) {
            case "default" -> "local";
            case "dev" -> "sbx";
            case "test" -> "dev";
            case "prod" -> "prd";
            default -> throw new IllegalArgumentException("Unsupported profile: " + activeProfile);
        };
    }

}
