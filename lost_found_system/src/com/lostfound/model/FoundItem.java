package com.lostfound.model;

import java.time.LocalDateTime;

public class FoundItem extends Item {

    private String category;
    private String placeFound;
    private String finderContact;

    public FoundItem(int itemId,
                     String itemName,
                     String date,
                     String category,
                     String placeFound,
                     String finderContact,
                     String user) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.date = date;
        this.category = category;
        this.placeFound = placeFound;
        this.finderContact = finderContact;

        this.status = "FOUND";

        setAuditInfo(user);
    }

    public FoundItem(int itemId,
                     String itemName,
                     String date,
                     String category,
                     String placeFound,
                     String finderContact,
                     String status,
                     String createdBy,
                     String createdAt,
                     String updatedBy,
                     String updatedAt) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.date = date;
        this.category = category;
        this.placeFound = placeFound;
        this.finderContact = finderContact;
        this.status = status;

        loadAuditInfo(createdBy, createdAt, updatedBy, updatedAt);
        updateCounter(itemId + 1);
    }

    @Override
    public void setAuditInfo(String user) {

        createdBy = user;
        createdAt = LocalDateTime.now().toString();
    }

    @Override
    public void updateAuditInfo(String user) {

        updatedBy = user;
        updatedAt = LocalDateTime.now().toString();
    }

    @Override
    public void display() {

        System.out.println(itemId + " | " + itemName + " | " + category + " | " + placeFound + " | " + status);
    }

    @Override
    public String toString() {

        return """
--------------------------------
Item ID     : %d
Item Name   : %s
Found Date  : %s
Category    : %s
Place Found : %s
Finder Phone: %s
Status      : %s
Created By  : %s
Created At  : %s
Updated By  : %s
Updated At  : %s
--------------------------------
""".formatted(itemId, itemName, date, category, placeFound, finderContact,
                status, createdBy, createdAt, updatedBy, updatedAt);
    }

    public String getCategory() {
        return category;
    }

    public String getPlaceFound() {
        return placeFound;
    }

    public String getFinderContact() {
        return finderContact;
    }
}