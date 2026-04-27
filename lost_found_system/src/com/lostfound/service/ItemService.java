package com.lostfound.service;

import com.lostfound.model.FoundItem;
import com.lostfound.model.LostItem;
import com.lostfound.repository.ItemRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ItemService {

    public static void addLostItem(LostItem item) {
        ItemRepository.addLostItem(item);
        System.out.println("Lost item recorded successfully.");
    }

    public static void addFoundItem(FoundItem item) {
        ItemRepository.addFoundItem(item);
        System.out.println("Found item recorded successfully.");

        Optional<LostItem> match = ItemRepository.findMatchFor(item);
        if (match.isPresent()) {
            LostItem lost = match.get();
            boolean resolved = ItemRepository.resolveMatch(lost.getItemId(), item.getItemId(), item.getCreatedBy());
            if (resolved) {
                System.out.println("✅ Match found! Lost item " + lost.getItemId()
                        + " is marked as RESOLVED based on the found item.");
            }
        } else {
            System.out.println("No matching lost item found at this time.");
        }
    }

    public static void viewLostItems() {
        List<LostItem> list = new ArrayList<>(ItemRepository.getLostItems());
        if (list.isEmpty()) {
            System.out.println("No lost items found.");
            return;
        }

        list.sort(Comparator.comparingInt(LostItem::getItemId));

        System.out.println("\n--- Lost Items ---");
        list.forEach(LostItem::display);
    }

    public static void viewFoundItems() {
        List<FoundItem> list = new ArrayList<>(ItemRepository.getFoundItems());
        if (list.isEmpty()) {
            System.out.println("No found items reported.");
            return;
        }

        list.sort(Comparator.comparingInt(FoundItem::getItemId));

        System.out.println("\n--- Found Items ---");
        list.forEach(FoundItem::display);
    }

    public static boolean resolveMatch(int lostId, int foundId, String resolvedBy) {
        boolean ok = ItemRepository.resolveMatch(lostId, foundId, resolvedBy);
        if (ok) {
            System.out.println("Resolved match between lost item " + lostId + " and found item " + foundId + ".");
        } else {
            System.out.println("Could not resolve match - check item IDs.");
        }
        return ok;
    }
}
