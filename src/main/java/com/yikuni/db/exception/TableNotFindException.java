package com.yikuni.db.exception;

public class TableNotFindException extends YikuniDBException{
    public TableNotFindException() {
    }

    public TableNotFindException(String message) {
        super(message);
    }

    public TableNotFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableNotFindException(Throwable cause) {
        super(cause);
    }

    public TableNotFindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
