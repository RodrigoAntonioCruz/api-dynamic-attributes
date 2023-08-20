package com.example.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class GenericException extends BusinessException {

	@Serial
	private static final long serialVersionUID = 3823335406673292457L;

	public GenericException(String object) {
		super.setHttpStatusCode(HttpStatus.BAD_REQUEST);
		super.setTimestamp(super.getTimestamp());
		super.setStatus(HttpStatus.BAD_REQUEST.value());
		super.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
		super.setDescription(object);
	}
}