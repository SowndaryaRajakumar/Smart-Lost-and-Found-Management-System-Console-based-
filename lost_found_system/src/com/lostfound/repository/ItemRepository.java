package com.lostfound.repository;

import com.lostfound.model.FoundItem;
import com.lostfound.model.LostItem;
import com.lostfound.service.FileService;

import java.util.*;
import java.util.stream.Collectors;

public class ItemRepository {

    private static final String LOST_FILE = "lost_items.txt";
    private static final String FOUND_FILE = "found_items.txt";

    private static final List<LostItem> lostItems = new ArrayList<>();
    private static final List<FoundItem> foundItems = new ArrayList<>();

    private static int nextId = 100;

    static {
        loadLostItems();
        loadFoundItems();
        recalcNextId();
        // Persist any normalization changes (e.g., duplicate IDs) back to disk.
        saveLostItems();
        saveFoundItems();
    }

    private static void recalcNextId() {
        // Determine the maximum ID used so far.
        int maxId = 100;
        for (LostItem item : lostItems) {
            maxId = Math.max(maxId, item.getItemId());
        }
        for (FoundItem item : foundItems) {
            maxId = Math.max(maxId, item.getItemId());
        }

        // Start assigning new IDs from above the current maximum.
        nextId = maxId + 1;

        // Resolve any duplicate IDs by assigning new unique ones.
        normalizeIds();

        // Recompute the next ID after normalization.
        maxId = 100;
        for (LostItem item : lostItems) {
            maxId = Math.max(maxId, item.getItemId());
        }
        for (FoundItem item : foundItems) {
            maxId = Math.max(maxId, item.getItemId());
        }
        nextId = maxId + 1;
    }

    private static void normalizeIds() {
        Set<Integer> seen = new HashSet<>();

        for (LostItem item : lostItems) {
            if (seen.contains(item.getItemId())) {
                item.setItemId(nextId++);
            }
            seen.add(item.getItemId());
        }

        for (FoundItem item : foundItems) {
            if (seen.contains(item.getItemId())) {
                item.setItemId(nextId++);
            }
            seen.add(item.getItemId());
        }
    }

    public static synchronized int getNextId() {
        // Ensure the nextId reflects the current stored data before handing out a new id.
        recalcNextId();
        return nextId++;
    }

    public static void addLostItem(LostItem item) {
        lostItems.add(item);
        saveLostItems();
    }

    public static void addFoundItem(FoundItem item) {
        foundItems.add(item);
        saveFoundItems();
    }

    public static List<LostItem> getLostItems() {
        return Collections.unmodifiableList(lostItems);
    }

    public static List<FoundItem> getFoundItems() {
        return Collections.unmodifiableList(foundItems);
    }

    public static Optional<LostItem> findLostById(int id) {
        return lostItems.stream().filter(i -> i.getItemId() == id).findFirst();
    }

    public static Optional<FoundItem> findFoundById(int id) {
        return foundItems.stream().filter(i -> i.getItemId() == id).findFirst();
    }

    public static Optional<LostItem> findMatchFor(FoundItem foundItem) {
        return lostItems.stream()
                .filter(l -> "OPEN".equalsIgnoreCase(l.getStatus()))
                .filter(l -> matchesText(l.getItemName(), foundItem.getItemName()))
                .filter(l -> matchesText(l.getPlace(), foundItem.getPlaceFound()))
                .findFirst();
    }

    private static boolean matchesText(String a, String b) {
        if (a == null || b == null) return false;
        a = a.trim().toLowerCase();
        b = b.trim().toLowerCase();
        return a.equals(b) || a.contains(b) || b.contains(a);
    }

    public static boolean resolveMatch(int lostId, int foundId, String resolvedBy) {
        Optional<LostItem> lostOpt = findLostById(lostId);
        Optional<FoundItem> foundOpt = findFoundById(foundId);

        if (lostOpt.isEmpty() || foundOpt.isEmpty())
            return false;

        LostItem lost = lostOpt.get();
        FoundItem found = foundOpt.get();

        lost.setStatus("RESOLVED");
        lost.updateAuditInfo(resolvedBy);

        found.setStatus("RESOLVED");
        found.updateAuditInfo(resolvedBy);

        saveLostItems();
        saveFoundItems();

        return true;
    }

    private static void loadLostItems() {
        List<String> lines = FileService.readLines(LOST_FILE);

        if (lines.isEmpty())
            return;

        boolean hasBlock = lines.stream().anyMatch(l -> l.trim().matches("(?i)item id:.*"));

        if (hasBlock) {
            parseOldFormatLostItems(lines);
        } else {
            lines.stream()
                    .map(ItemRepository::parseLostItemLine)
                    .filter(Objects::nonNull)
                    .forEach(lostItems::add);
        }

        // Always rewrite so that the file stays in canonical key:value block format.
        saveLostItems();
    }

    private static void loadFoundItems() {
        List<String> lines = FileService.readLines(FOUND_FILE);

        if (lines.isEmpty())
            return;

        boolean hasBlock = lines.stream().anyMatch(l -> l.trim().matches("(?i)item id:.*"));

        if (hasBlock) {
            parseOldFormatFoundItems(lines);
        } else {
            lines.stream()
                    .map(ItemRepository::parseFoundItemLine)
                    .filter(Objects::nonNull)
                    .forEach(foundItems::add);
        }

        // Always rewrite so that the file stays in canonical key:value block format.
        saveFoundItems();
    }

    private static void saveLostItems() {
        List<String> lines = new ArrayList<>();
        for (LostItem item : lostItems) {
            lines.addAll(formatLostItem(item));
            lines.add("---");
        }
        FileService.overwrite(LOST_FILE, lines);
    }

    private static void saveFoundItems() {
        List<String> lines = new ArrayList<>();
        for (FoundItem item : foundItems) {
            lines.addAll(formatFoundItem(item));
            lines.add("---");
        }
        FileService.overwrite(FOUND_FILE, lines);
    }

    private static List<String> formatLostItem(LostItem item) {
        List<String> lines = new ArrayList<>();
        lines.add("Item ID: " + item.getItemId());
        lines.add("Item Name: " + safe(item.getItemName()));
        lines.add("Lost Date: " + safe(item.getDate()));
        lines.add("Marks: " + safe(item.getMarks()));
        lines.add("Place: " + safe(item.getPlace()));
        lines.add("Contact: " + safe(item.getContactNumber()));
        lines.add("Image Path: " + safe(item.getImagePath()));
        lines.add("Description: " + safe(item.getDescription()));
        lines.add("Status: " + safe(item.getStatus()));
        lines.add("Created By: " + safe(item.getCreatedBy()));
        lines.add("Created At: " + safe(item.getCreatedAt()));
        lines.add("Updated By: " + safe(item.getUpdatedBy()));
        lines.add("Updated At: " + safe(item.getUpdatedAt()));
        return lines;
    }

    private static List<String> formatFoundItem(FoundItem item) {
        List<String> lines = new ArrayList<>();
        lines.add("Item ID: " + item.getItemId());
        lines.add("Item Name: " + safe(item.getItemName()));
        lines.add("Found Date: " + safe(item.getDate()));
        lines.add("Category: " + safe(item.getCategory()));
        lines.add("Place Found: " + safe(item.getPlaceFound()));
        lines.add("Finder Contact: " + safe(item.getFinderContact()));
        lines.add("Status: " + safe(item.getStatus()));
        lines.add("Created By: " + safe(item.getCreatedBy()));
        lines.add("Created At: " + safe(item.getCreatedAt()));
        lines.add("Updated By: " + safe(item.getUpdatedBy()));
        lines.add("Updated At: " + safe(item.getUpdatedAt()));
        return lines;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static LostItem parseLostItemLine(String line) {
        if (line == null || line.isBlank())
            return null;

        String[] tokens = line.split("\\|", -1);
        if (tokens.length < 13)
            return null;

        try {
            int id = Integer.parseInt(tokens[0]);
            String itemName = tokens[1];
            String date = tokens[2];
            String marks = tokens[3];
            String place = tokens[4];
            String contact = tokens[5];
            String imagePath = tokens[6];
            String description = tokens[7];
            String status = tokens[8];
            String createdBy = tokens[9];
            String createdAt = tokens[10];
            String updatedBy = tokens[11];
            String updatedAt = tokens[12];

            return new LostItem(id, itemName, date, marks, place, contact, imagePath,
                    description, status, createdBy, createdAt, updatedBy, updatedAt);
        } catch (Exception e) {
            return null;
        }
    }

    private static FoundItem parseFoundItemLine(String line) {
        if (line == null || line.isBlank())
            return null;

        String[] tokens = line.split("\\|", -1);
        if (tokens.length < 10)
            return null;

        try {
            int id = Integer.parseInt(tokens[0]);
            String itemName = tokens[1];
            String date = tokens[2];
            String category;
            String placeFound;
            String finderContact;
            String status;
            String createdBy;
            String createdAt;
            String updatedBy;
            String updatedAt;

            if (tokens.length >= 11 && !tokens[3].isBlank()) {
                // Modern format: id|itemName|date|category|placeFound|finderContact|status|...
                category = tokens[3];
                placeFound = tokens[4];
                finderContact = tokens[5];
                status = tokens[6];
                createdBy = tokens[7];
                createdAt = tokens[8];
                updatedBy = tokens[9];
                updatedAt = tokens[10];
            } else {
                // Legacy format (or empty category): id|itemName|date|category|placeFound|status|createdBy|createdAt|updatedBy|updatedAt
                category = tokens[3].isBlank() ? tokens[4] : tokens[3];
                placeFound = tokens[3].isBlank() ? tokens[5] : tokens[4];
                finderContact = "";
                status = tokens[3].isBlank() ? tokens[6] : tokens[5];
                createdBy = tokens[3].isBlank() ? tokens[7] : tokens[6];
                createdAt = tokens[3].isBlank() ? tokens[8] : tokens[7];
                updatedBy = tokens[3].isBlank() ? tokens[9] : tokens[8];
                updatedAt = tokens[3].isBlank() ? tokens[10] : tokens[9];
            }

            return new FoundItem(id, itemName, date, category, placeFound, finderContact,
                    status, createdBy, createdAt, updatedBy, updatedAt);
        } catch (Exception e) {
            return null;
        }
    }


    private static void parseOldFormatLostItems(List<String> lines) {
        Map<String, String> map = new HashMap<>();
        for (String line : lines) {
            if (line.trim().startsWith("---")) {
                if (!map.isEmpty()) {
                    LostItem item = buildLostItemFromMap(map);
                    if (item != null) {
                        lostItems.add(item);
                    }
                    map.clear();
                }
                continue;
            }

            int colon = line.indexOf(":");
            if (colon > 0) {
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                map.put(key, value);
            }
        }

        if (!map.isEmpty()) {
            LostItem item = buildLostItemFromMap(map);
            if (item != null) {
                lostItems.add(item);
            }
        }
    }

    private static void parseOldFormatFoundItems(List<String> lines) {
        Map<String, String> map = new HashMap<>();
        for (String line : lines) {
            if (line.trim().startsWith("---")) {
                if (!map.isEmpty()) {
                    FoundItem item = buildFoundItemFromMap(map);
                    if (item != null) {
                        foundItems.add(item);
                    }
                    map.clear();
                }
                continue;
            }

            int colon = line.indexOf(":");
            if (colon > 0) {
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                map.put(key, value);
            }
        }

        if (!map.isEmpty()) {
            FoundItem item = buildFoundItemFromMap(map);
            if (item != null) {
                foundItems.add(item);
            }
        }
    }

    private static LostItem buildLostItemFromMap(Map<String, String> map) {
        try {
            int id = Integer.parseInt(map.getOrDefault("Item ID", "0"));
            String itemName = map.getOrDefault("Item Name", "");
            String date = map.getOrDefault("Lost Date", "");
            String marks = map.getOrDefault("Marks", "");
            String place = map.getOrDefault("Place", "");
            String contact = map.getOrDefault("Contact", "");
            String imagePath = map.getOrDefault("Image Path", "");
            String description = map.getOrDefault("Description", "");
            String status = map.getOrDefault("Status", "OPEN");
            String createdBy = map.getOrDefault("Created By", "");
            String createdAt = map.getOrDefault("Created At", "");
            String updatedBy = map.getOrDefault("Updated By", "");
            String updatedAt = map.getOrDefault("Updated At", "");

            return new LostItem(id, itemName, date, marks, place, contact, imagePath,
                    description, status, createdBy, createdAt, updatedBy, updatedAt);
        } catch (Exception e) {
            return null;
        }
    }

    private static FoundItem buildFoundItemFromMap(Map<String, String> map) {
        try {
            int id = Integer.parseInt(map.getOrDefault("Item ID", "0"));
            String itemName = map.getOrDefault("Item Name", "");
            String date = map.getOrDefault("Found Date", "");
            String category = map.getOrDefault("Category", "");
            String placeFound = map.getOrDefault("Place Found", "");
            String finderPhone = map.getOrDefault("Finder Contact", map.getOrDefault("Finder Phone", ""));
            String status = map.getOrDefault("Status", "FOUND");
            String createdBy = map.getOrDefault("Created By", "");
            String createdAt = map.getOrDefault("Created At", "");
            String updatedBy = map.getOrDefault("Updated By", "");
            String updatedAt = map.getOrDefault("Updated At", "");

            return new FoundItem(id, itemName, date, category, placeFound, finderPhone,
                    status, createdBy, createdAt, updatedBy, updatedAt);
        } catch (Exception e) {
            return null;
        }
    }
}

