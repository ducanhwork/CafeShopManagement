package com.group3.application.common.util;

public class HttpException extends Exception {
    public final int code;
    public final String body;
    public HttpException(int code, String body){
        super("HTTP " + code);
        this.code = code;
        this.body = body;
    }
}
