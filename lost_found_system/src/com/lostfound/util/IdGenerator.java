package com.lostfound.util;

public class IdGenerator {

    private static int userId = 1;
    private static int itemId = 100;

    public static int generateUserId() {
        return userId++;
    }

    public static int generateItemId() {
        return itemId++;
    }
}