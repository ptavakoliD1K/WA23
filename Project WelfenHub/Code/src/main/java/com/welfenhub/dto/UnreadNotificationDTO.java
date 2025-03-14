package com.welfenhub.dto;

public class UnreadNotificationDTO {
    private Long chatRoomId;
    private int unreadCount;

    public UnreadNotificationDTO() {
    }

    public UnreadNotificationDTO(Long chatRoomId, int unreadCount) {
        this.chatRoomId = chatRoomId;
        this.unreadCount = unreadCount;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
