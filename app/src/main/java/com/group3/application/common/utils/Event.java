package com.group3.application.common.utils;


/**
 * Được sử dụng làm wrapper cho dữ liệu được hiển thị trong LiveData
 * và chỉ nên được tiêu thụ một lần (ví dụ: điều hướng, hiển thị Toast).
 */
public class Event<T> {

    private T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Trả về nội dung và ngăn chặn việc sử dụng lại nó.
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
     * Trả về nội dung ngay cả khi nó đã được xử lý.
     */
    public T peekContent() {
        return content;
    }
}