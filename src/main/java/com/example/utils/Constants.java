package com.example.utils;


public class Constants {

    private Constants() {
    }

    public static final String LOG_KEY_METHOD = "method={} ";
    public static final String LOG_KEY_MESSAGE = "msg=\"{}\" ";
    public static final String LOG_KEY_EVENT = "event={} ";
    public static final String LOG_KEY_DESCRIPTION = "description=\"{}\" ";
    public static final String LOG_KEY_HTTP_CODE = "httpCode={} ";
    public static final String LOG_EXCEPTION = "br.com.example.exception={} ";
    public static final String X_RD_TRACEID = "X-rd-traceid";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String NOT_FOUND = "Objeto não encontrado";
    public static final String CONSTRAINT_VALIDATION_FAILED = "Constraint validation failed";
    public static final String EMPTY = "";
    public static final String DUPLICATION_CODE = "E11000";
    public static final String INVALID_DATE_FORMAT = "Formato de data inválido: ";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String KEY_NAME = "name";
    public static final String OPTION_I = "i";
    public static final String KEY_ATTRIBUTES = "attributes";
    public static final String KEY_VALUE = "value";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final Integer BATCH_SIZE = 15000;
    public static final Integer ONE = 1;
    public static final String KEY_ID = "id";
    public static final String DUPLICATION_DESCRIPTION = "Já existe uma descrição cadastrada com esse conteúdo";
    public static final String DESCRIPTION_NOT_NULL = "A descrição não pode ser nula";
    public static final String DESCRIPTION_MAX_LENGTH = "O tamanho do campo descrição deve ter entre 3 e 1000 caracteres";
}
