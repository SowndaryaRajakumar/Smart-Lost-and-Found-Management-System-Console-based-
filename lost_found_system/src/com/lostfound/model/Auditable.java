package com.lostfound.model;

public interface Auditable {

    void setAuditInfo(String createdBy);

    void updateAuditInfo(String updatedBy);
}