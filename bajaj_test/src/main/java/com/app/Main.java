package com.app;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar test.jar <PRN Number> <path/to/json/file>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replace(" ", ""); // PRN number in all lowercase without spaces
        String jsonFilePath = args[1];

        // Parse JSON file
        String destinationValue = parseJsonForDestination(jsonFilePath);
        if (destinationValue == null) {
            System.out.println("No 'destination' key found in the JSON file.");
            return;
        }

        // Generate a random alphanumeric string of size 8
        String randomString = generateRandomString(8);

        // Create the concatenated string
        String concatenatedValue = prnNumber + destinationValue + randomString;

        // Generate MD5 Hash
        String md5Hash = generateMD5Hash(concatenatedValue);

        // Output in the required format
        String output = md5Hash + ";" + randomString;
        System.out.println(output);
    }

    private static String parseJsonForDestination(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(new File(filePath));
            return findFirstDestination(root);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String findFirstDestination(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode childNode = node.get(fieldName);
                if (fieldName.equals("destination")) {
                    return childNode.asText();
                }
                String result = findFirstDestination(childNode);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode childNode : node) {
                String result = findFirstDestination(childNode);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
