package ru.hse.avrogen.util.exceptions;

public class ApicurioClientException extends RuntimeException {

    // Todo: static factory method with string format.

    public ApicurioClientException() {
    }

    public ApicurioClientException(String message) {
        super(message);
    }

    public ApicurioClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApicurioClientException(Throwable cause) {
        super(cause);
    }

    public ApicurioClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
