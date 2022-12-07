package com.yikuni.db.exception;

public class YikuniDBException extends Exception{
    public YikuniDBException() {
    }

    public YikuniDBException(String message) {
        super(message);
    }

    public YikuniDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public YikuniDBException(Throwable cause) {
        super(cause);
    }

    public YikuniDBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
