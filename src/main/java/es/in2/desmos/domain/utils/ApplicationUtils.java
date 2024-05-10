package es.in2.desmos.domain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.in2.desmos.domain.exceptions.JsonReadingException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ApplicationUtils {

    private ApplicationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String calculateSHA256(String data) throws NoSuchAlgorithmException, JsonProcessingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.readTree(data);
            String sortedData = sortAttributesAlphabetically(data);
            byte[] result = messageDigest.digest(sortedData.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(result);
        } catch (JsonProcessingException e) {
            byte[] result = messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(result);
        }
    }


    public static String calculateHashLink(String previousHash, String entityHash) throws NoSuchAlgorithmException, JsonProcessingException {
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

    //    TODO: This method will be deprecated in the future
//    public static boolean checkIfHashLinkExistInDataLocation(String urlString) {
//        try {
//            URL url = URI.create(urlString).toURL();
//            Map<String, String> queryParams = splitQuery(url);
//            log.debug("Query params: {}", queryParams);
//            if (queryParams.containsKey("hl")) {
//                log.debug("Query param hl: {}", queryParams.get("hl"));
//                return queryParams.containsKey("hl");
//            } else {
//                throw new HashLinkException("Query param hl not found");
//            }
//        } catch (IllegalArgumentException | MalformedURLException e) {
//            throw new HashLinkException("Error parsing dataLocation");
//        }
//    }

    //TODO: its use must be analyzed
//    private static Map<String, String> splitQuery(URL url) {
//        if (url.getQuery() == null || url.getQuery().isEmpty()) {
//            return new HashMap<>();
//        }
//        Map<String, String> queryPairs = new HashMap<>();
//        String[] pairs = url.getQuery().split("&");
//        for (String pair : pairs) {
//            int idx = pair.indexOf("=");
//            queryPairs.put(pair.substring(0, idx), idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null);
//        }
//        return queryPairs;
//    }

    public static String getEnvironmentMetadata(String activeProfile) {

        return switch (activeProfile) {
            case "default" -> "local";
            case "dev" -> "sbx";
            case "test" -> "dev";
            case "prod" -> "prd";
            default -> throw new IllegalArgumentException("Unsupported profile: " + activeProfile);
        };
    }

    private static String sortAttributesAlphabetically(String retrievedBrokerEntity) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode retrievedBrokerEntityJson = objectMapper.readTree(retrievedBrokerEntity);
        if (retrievedBrokerEntityJson.isObject()) {
            return sortJsonObject(objectMapper, retrievedBrokerEntityJson);
        } else if (retrievedBrokerEntityJson.isArray()) {
            return sortJsonArray(objectMapper, retrievedBrokerEntityJson);
        } else {
            return objectMapper.writeValueAsString(retrievedBrokerEntityJson);
        }
    }

    private static String sortJsonObject(ObjectMapper objectMapper, JsonNode jsonObject) throws JsonProcessingException {
        TreeMap<String, JsonNode> sortedMap = new TreeMap<>();
        Iterator<Map.Entry<String, JsonNode>> jsonNodeFields = jsonObject.fields();
        while (jsonNodeFields.hasNext()) {
            Map.Entry<String, JsonNode> entry = jsonNodeFields.next();
            if (entry.getValue().isObject() || entry.getValue().isArray()) {
                sortedMap.put(entry.getKey(), objectMapper.readTree(sortAttributesAlphabetically(entry.getValue().toString())));
            } else {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        ObjectNode sortedObjectNode = objectMapper.createObjectNode();
        sortedMap.forEach(sortedObjectNode::set);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedObjectNode);
    }

    private static String sortJsonArray(ObjectMapper objectMapper, JsonNode jsonArray) throws JsonProcessingException {
        ArrayNode arrayNode = (ArrayNode) jsonArray;
        ArrayNode sortedArrayNode = objectMapper.createArrayNode();
        arrayNode.forEach(node -> {
            try {
                if (node.isObject()) {
                    sortedArrayNode.add(objectMapper.readTree(sortAttributesAlphabetically(node.toString())));
                } else {
                    sortedArrayNode.add(node);
                }
            } catch (JsonProcessingException e) {
                throw new JsonReadingException("Error occurred while parsing JSON: " + e.getMessage());
            }
        });
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedArrayNode);
    }

}