package com.tourflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class SlotNotAvailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SlotNotAvailableException(String message) {
        super(message);
    }

    public SlotNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
