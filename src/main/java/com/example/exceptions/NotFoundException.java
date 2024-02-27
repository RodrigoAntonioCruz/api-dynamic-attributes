package com.example.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class NotFoundException extends BusinessException {
	@Serial
	private static final long serialVersionUID = 3823335406673292457L;
	public NotFoundException(String object) {
		super.setHttpStatusCode(HttpStatus.NOT_FOUND);
		super.setTimestamp(super.getTimestamp());
		super.setStatus(HttpStatus.NOT_FOUND.value());
		super.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
		super.setDescription(object);
	}
}