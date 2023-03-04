package ru.hse.avrogen.util.exceptions;

public abstract class AvroGeneratorRuntimeException extends RuntimeException {

    public AvroGeneratorRuntimeException() {
    }

    public AvroGeneratorRuntimeException(String message) {
        super(message);
    }

    public AvroGeneratorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvroGeneratorRuntimeException(Throwable cause) {
        super(cause);
    }

    public AvroGeneratorRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public abstract String toJSON();
}
