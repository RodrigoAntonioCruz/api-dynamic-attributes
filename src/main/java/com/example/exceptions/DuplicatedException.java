package com.example.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class DuplicatedException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 8379432021656028515L;

    public DuplicatedException(String object) {
        super.setHttpStatusCode(HttpStatus.CONFLICT);
        super.setTimestamp(super.getTimestamp());
        super.setStatus(HttpStatus.CONFLICT.value());
        super.setMessage(HttpStatus.CONFLICT.getReasonPhrase());
        super.setDescription(object);
    }

}
