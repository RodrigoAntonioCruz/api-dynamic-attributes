package com.example.utils;


public class Constants {

    private Constants() {
    }

    /**
     * LOG_KEY
     */
    public static final String LOG_KEY_METHOD = "method={} ";
    public static final String LOG_KEY_MESSAGE = "msg=\"{}\" ";

    public static final String LOG_KEY_CAUSE = "cause=\"{}\" ";
    public static final String LOG_KEY_ENTITY = "entity=\"{}\" ";
    public static final String LOG_KEY_ENTITY_ID = "entityId={} ";
    public static final String LOG_KEY_EVENT = "event={} ";

    public static final String LOG_KEY_FILTER = "filter={} ";
    public static final String LOG_KEY_DESCRIPTION = "description=\"{}\" ";
    public static final String LOG_KEY_HTTP_CODE = "httpCode={} ";


    /**
     * LOG_METHOD
     * */
    public static final String LOG_METHOD_FIND = "find";
    public static final String LOG_METHOD_FIND_BY_ID = "findById";
    public static final String LOG_METHOD_FIND_PRODUCTS = "findProducts";
    public static final String LOG_METHOD_CREATE_WHISHLIST = "createWishlist";
    public static final String LOG_METHOD_FIND_ALL_PRODUCTS_BY_WHISHLIST = "findAllProductByWishlistIdOrCustomerId";
    public static final String LOG_METHOD_FIND_IF_EXISTS_PRODUCTS_BY_WHISHLIST_ID = "findIfExistsProductByWhishlistId";
    public static final String LOG_METHOD_ADD_WHISHLIST = "add";
    public static final String LOG_METHOD_REMOVE_PRODUCT_WHISHLIST = "remove";
    public static final String LOG_METHOD_CREATE = "create";
    public static final String LOG_METHOD_UPDATE = "update";
    public static final String LOG_METHOD_DELETE = "delete";

    public static final String LOG_METHOD_GET_EXCEPTION = "getException";

    /**
     * LOG_OTHER
     */
    public static final String LOG_EXCEPTION = "br.com.example.exception={} ";

    public static final String LOG_WARN_ERROR = "logWarnError={} ";
    public static final String X_RD_TRACEID = "X-rd-traceid";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String NOT_FOUND = "Objeto não encontrado";
    public static final String CONSTRAINT_VALIDATION_FAILED = "Constraint validation failed";
    public static final String NOT_FOUND_WISHLIST = "Lista de desejos não encontrada";
    public static final String NOT_FOUND_CUSTOMER = "Cliente não encontrado";
    public static final String NOT_FOUND_PRODUCT = "Produto não encontrado";
    public static final String LIMIT_PRODUCTS = "A lista de desejos deve ter no máximo 20 produtos";
    public static final Integer LIMIT_LIST_PRODUCTS = 20;
    public static final String WISHLIST_EMPTY = "Você deve atribuir pelo menos um produto à lista de desejos!";
    public static final String EMPTY = "";
    public static final String DUPLICATION_CODE = "E11000";
    public static final String KEY_CPF = "cpf";
    public static final String KEY_EMAIL = "email";
    public static final String DUPLICATION_WISHLIST = "Já existe uma lista de desejos com esse CPF e nome";
    public static final String DUPLICATION_CPF = "Já existe um CPF cadastrado com esse número";
    public static final String DUPLICATION_EMAIL = "Já existe um e-mail cadastrado com esse endereço";
    public static final String EMPTY_LIST = "Sua lista de desejos está vazia";
    public static final String INVALID_OWNER_WISHLIST = "A lista de desejos não corresponde ao seu ID ou CPF. Por favor, verifique se o ID ou CPF estão corretos antes de realizar a operação.";
    public static final String NO_RESULT_FOUND = "No Results Found";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String DUPLICATION_TITLE = "Já existe um título cadastrado com esse conteúdo";
    public static final String DUPLICATION_DESCRIPTION = "Já existe uma descrição cadastrada com esse conteúdo";
    public static final String DUPLICATION_IMAGE = "Já existe uma imagem cadastrada com esse conteúdo";
    public static final String CPF_NOT_NULL = "O CPF não pode ser nulo";
    public static final String CPF_MAX_LENGTH = "O tamanho do campo CPF deve ter somente 11 dígitos sem caracteres especiais";
    public static final String CPF_INVALID = "O CPF digitado é inválido";
    public static final String EMAIL_INVALID = "O e-mail digitado é inválido";
    public static final String EMAIL_NOT_NULL = "O e-mail não pode ser nulo";
    public static final String EMAIL_MAX_LENGTH = "O tamanho do campo e-mail deve ter somente até 120 caracteres";
    public static final String TITLE_NOT_NULL = "O título não pode ser nulo";
    public static final String TITLE_MAX_LENGTH ="O tamanho do campo título deve ter entre 3 e 255 caracteres";
    public static final String DESCRIPTION_NOT_NULL = "A descrição não pode ser nula";
    public static final String DESCRIPTION_MAX_LENGTH = "O tamanho do campo descrição deve ter entre 3 e 1000 caracteres";
    public static final String IMAGE_NOT_NULL = "A imagem não pode ser nula";
    public static final String IMAGE_MAX_LENGTH = "O tamanho do campo imagem deve ter entre 3 e 255 caracteres";
    public static final String PRICE_NOT_NULL = "O preço não pode ser nulo";
    public static final String PRICE_ZERO = "0.0";
    public static final String PRICE_GREATER_THAN_ZERO = "O preço deve ser maior que zero";
    public static final String NAME_NOT_NULL = "O nome não pode ser nulo";
    public static final String NAME_MAX_LENGTH = "O tamanho do campo nome deve ter entre 3 e 120 caracteres";
    public static final String CLIENT_ID_NOT_NULL = "O id do cliente não pode ser nulo";
}
