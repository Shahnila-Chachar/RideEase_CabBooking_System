package com.rideease.model;

import java.time.LocalDateTime;

public class Notification {
    private int notificationId;
    private int userId;
    private Integer bookingId;
    private String message;
    private String notificationType;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification(int notificationId, int userId, Integer bookingId,
                        String message, String notificationType,
                        boolean isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.bookingId = bookingId;
        this.message = message;
        this.notificationType = notificationType;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int getNotificationId() { return notificationId; }
    public int getUserId() { return userId; }
    public Integer getBookingId() { return bookingId; }
    public String getMessage() { return message; }
    public String getNotificationType() { return notificationType; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public void setMessage(String message) { this.message = message; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
