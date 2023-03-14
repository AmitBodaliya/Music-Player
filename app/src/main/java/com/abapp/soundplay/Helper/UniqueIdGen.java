package com.abapp.soundplay.Helper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class UniqueIdGen {
    private static UniqueIdGen instance;
    private Set<String> usedIds;

    private UniqueIdGen() {
        usedIds = new HashSet<>();
    }

    public static synchronized UniqueIdGen getInstance() {
        if (instance == null) {
            instance = new UniqueIdGen();
        }
        return instance;
    }

    public String generateUniqueId() {
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String id;

        do {
            // Generate a random 10-character ID
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                int randomIndex = random.nextInt(characters.length());
                sb.append(characters.charAt(randomIndex));
            }
            id = sb.toString();
        } while (usedIds.contains(id));

        // Add the generated ID to the set of used IDs
        usedIds.add(id);

        return id;
    }
}

