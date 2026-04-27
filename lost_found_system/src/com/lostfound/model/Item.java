package com.lostfound.model;

public abstract class Item implements Auditable {

    protected static int counter = 100;

    protected int itemId;
    protected String itemName;
    protected String date;
    protected String status;

    protected String createdBy;
    protected String createdAt;

    protected String updatedBy;
    protected String updatedAt;

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void loadAuditInfo(String createdBy, String createdAt,
                              String updatedBy, String updatedAt) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public static void updateCounter(int next) {
        if (next > counter) {
            counter = next;
        }
    }

    public static int getNextId() {
        return counter++;
    }

    public abstract void display();
}