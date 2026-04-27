package com.lostfound.repository;

import com.lostfound.model.User;
import com.lostfound.service.FileService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    private static final String USERS_FILE = "users.txt";
    private static List<User> users = new ArrayList<>();

    static {
        loadUsers();
    }

    public static void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public static User findUser(String username) {
        if (username == null) return null;

        for (User u : users) {
            if (username.equals(u.getUsername()))
                return u;
        }

        return null;
    }

    public static List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    private static void loadUsers() {
        List<String> lines = FileService.readLines(USERS_FILE);

        if (lines.isEmpty()) {
            return;
        }

        if (lines.stream().anyMatch(l -> l.trim().toLowerCase().startsWith("user id:"))) {
            parseOldFormatUsers(lines);
        } else {
            for (String line : lines) {
                User user = parseUserLine(line);
                if (user != null) {
                    users.add(user);
                }
            }
        }

        saveUsers();
    }

    private static void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.addAll(formatUser(user));
            lines.add("---");
        }
        FileService.overwrite(USERS_FILE, lines);
    }

    private static List<String> formatUser(User user) {
        List<String> lines = new ArrayList<>();
        lines.add("User ID: " + user.getUserId());
        lines.add("Username: " + safe(user.getUsername()));
        lines.add("Password: " + safe(user.getPassword()));
        lines.add("Phone: " + safe(user.getPhone()));
        lines.add("Email: " + safe(user.getEmail()));
        lines.add("Role: " + safe(user.getRole()));
        lines.add("Created By: " + safe(user.getCreatedBy()));
        lines.add("Created At: " + (user.getCreatedAt() == null ? "" : user.getCreatedAt().toString()));
        lines.add("Updated By: " + safe(user.getUpdatedBy()));
        lines.add("Updated At: " + (user.getUpdatedAt() == null ? "" : user.getUpdatedAt().toString()));
        return lines;
    }

    private static void parseOldFormatUsers(List<String> lines) {
        Map<String, String> map = new HashMap<>();
        for (String line : lines) {
            if (line.trim().equals("---")) {
                addUserFromMap(map);
                map.clear();
                continue;
            }
            int colon = line.indexOf(":");
            if (colon > 0) {
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                map.put(key, value);
            }
        }
        addUserFromMap(map);
    }

    private static void addUserFromMap(Map<String, String> map) {
        if (map.isEmpty()) return;

        try {
            int userId = Integer.parseInt(map.getOrDefault("User ID", "0"));
            String username = map.getOrDefault("Username", "");
            String password = map.getOrDefault("Password", "");
            String phone = map.getOrDefault("Phone", "");
            String email = map.getOrDefault("Email", "");
            String role = map.getOrDefault("Role", "USER");
            String createdBy = map.getOrDefault("Created By", "");
            String createdAt = map.getOrDefault("Created At", "");
            String updatedBy = map.getOrDefault("Updated By", "");
            String updatedAt = map.getOrDefault("Updated At", "");

            users.add(new User(userId, username, password, phone, email, role,
                    createdBy, createdAt, updatedBy, updatedAt));
        } catch (Exception e) {
            // ignore invalid entries
        }
    }

    private static User parseUserLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] tokens = line.split(",", -1);
        if (tokens.length < 5) {
            return null;
        }

        try {
            int userId = Integer.parseInt(tokens[0]);
            String username = tokens[1];
            String password = tokens[2];
            String phone = tokens[3];
            String email = tokens[4];
            String role = tokens.length > 5 ? tokens[5] : "USER";

            return new User(userId, username, password, phone, email, role, "system", "", "", "");
        } catch (Exception e) {
            return null;
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
