package com.lostfound.model;

import java.time.LocalDateTime;

public class LostItem extends Item {

    private String marks;
    private String place;
    private String contactNumber;
    private String imagePath;
    private String description;

    public LostItem(int itemId,
                    String itemName,
                    String date,
                    String marks,
                    String place,
                    String contactNumber,
                    String imagePath,
                    String description,
                    String user) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.date = date;
        this.marks = marks;
        this.place = place;
        this.contactNumber = contactNumber;
        this.imagePath = imagePath;
        this.description = description;

        this.status = "OPEN";

        setAuditInfo(user);
    }

    public LostItem(int itemId,
                    String itemName,
                    String date,
                    String marks,
                    String place,
                    String contactNumber,
                    String imagePath,
                    String description,
                    String status,
                    String createdBy,
                    String createdAt,
                    String updatedBy,
                    String updatedAt) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.date = date;
        this.marks = marks;
        this.place = place;
        this.contactNumber = contactNumber;
        this.imagePath = imagePath;
        this.description = description;
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

        System.out.println(itemId + " | " + itemName + " | " + place + " | " + status);
    }

    @Override
    public String toString() {

        return """
--------------------------------
Item ID     : %d
Item Name   : %s
Lost Date   : %s
Place       : %s
Marks       : %s
Contact     : %s
Image Path  : %s
Description : %s
Status      : %s
Created By  : %s
Created At  : %s
Updated By  : %s
Updated At  : %s
--------------------------------
""".formatted(itemId, itemName, date, place, marks, contactNumber, imagePath,
                description, status, createdBy, createdAt, updatedBy, updatedAt);
    }

    public String getMarks() {
        return marks;
    }

    public String getPlace() {
        return place;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }
}