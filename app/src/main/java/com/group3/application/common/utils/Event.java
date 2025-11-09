package com.group3.application.common.utils;

/**
 * Lớp tiện ích để bao bọc dữ liệu của LiveData, đại diện cho một sự kiện.
 * Đảm bảo một sự kiện chỉ được xử lý một lần.
 */
public class Event<T> {

    private T content;

    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Trả về nội dung và đánh dấu là đã được xử lý.
     * Nếu đã xử lý rồi, trả về null.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Kiểm tra xem sự kiện đã được xử lý chưa.
     */
    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}
