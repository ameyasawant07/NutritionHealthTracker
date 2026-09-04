package com.example.nutritionhealthtracker.models;

import org.json.JSONException;
import org.json.JSONObject;

public class PendingSyncItem {
    private String recordId;
    private String userId;
    private String deviceId;
    private String modelType; // WEIGHT, BMI, NUTRITION, WATER, EXERCISE, SLEEP, REPORT, PROFILE, etc.
    private String action; // CREATE, UPDATE, DELETE
    private long updatedAt;
    private String syncStatus; // PENDING_SYNC, SYNCING, SYNCED
    private String payloadJson;

    public PendingSyncItem() {
    }

    public PendingSyncItem(String recordId, String userId, String deviceId, String modelType,
                           String action, long updatedAt, String syncStatus, String payloadJson) {
        this.recordId = recordId;
        this.userId = userId;
        this.deviceId = deviceId;
        this.modelType = modelType;
        this.action = action;
        this.updatedAt = updatedAt;
        this.syncStatus = syncStatus;
        this.payloadJson = payloadJson;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("recordId", recordId);
            obj.put("userId", userId);
            obj.put("deviceId", deviceId);
            obj.put("modelType", modelType);
            obj.put("action", action);
            obj.put("updatedAt", updatedAt);
            obj.put("syncStatus", syncStatus);
            obj.put("payloadJson", payloadJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static PendingSyncItem fromJson(JSONObject obj) {
        if (obj == null) return null;
        PendingSyncItem item = new PendingSyncItem();
        item.recordId = obj.optString("recordId", "");
        item.userId = obj.optString("userId", "");
        item.deviceId = obj.optString("deviceId", "");
        item.modelType = obj.optString("modelType", "");
        item.action = obj.optString("action", "UPDATE");
        item.updatedAt = obj.optLong("updatedAt", System.currentTimeMillis());
        item.syncStatus = obj.optString("syncStatus", "PENDING_SYNC");
        item.payloadJson = obj.optString("payloadJson", "{}");
        return item;
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
