package com.candidate.pixkeymanagement.controller;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.exception.BadRequestException;
import com.candidate.pixkeymanagement.exception.NotFoundException;
import com.candidate.pixkeymanagement.exception.UnexpectedException;
import com.candidate.pixkeymanagement.exception.UnprocessableEntityException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.candidate.pixkeymanagement.util.MessageConstant.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class PixKeyManagementControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.debug("Handling MethodArgumentNotValidException", ex);
        List<ErrorMessageDTO> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(createError(error, request.getClass())));
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.debug("Handling HttpMessageNotReadableException", ex);
        if (ex.getCause() instanceof JsonMappingException cause) {
            String field = cause.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("."));
            return handleExceptionInternal(ex, new ErrorMessageDTO(FIELD_INVALID, field), headers, HttpStatus.BAD_REQUEST, request);
        }
        return handleExceptionInternal(ex, null, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        log.debug("MissingServletRequestParameterException", ex);
        ErrorMessageDTO error = new ErrorMessageDTO(FIELD_REQUIRED, ex.getParameterName());
        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.debug("MethodArgumentTypeMismatchException", ex);
        List<ErrorMessageDTO> errors = new ArrayList<>();
        errors.add(new ErrorMessageDTO(FIELD_INVALID, ex.getParameter().getParameterName()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log.debug("BadRequestException", ex);
        return handleExceptionInternal(ex, ex.getError(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = UnprocessableEntityException.class)
    protected ResponseEntity<Object> handleUnprocessableEntityException(UnprocessableEntityException ex, WebRequest request) {
        log.debug("UnprocessableEntityException", ex);
        return handleExceptionInternal(ex, ex.getErrors(), new HttpHeaders(), ex.getStatus(), request);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.debug("NotFoundException", ex);
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = UnexpectedException.class)
    protected ResponseEntity<Object> handleUnexpectedException(UnexpectedException ex, WebRequest request) {
        log.debug("UnexpectedException", ex);
        return handleExceptionInternal(ex, new ErrorMessageDTO(UNEXPECTED_ERROR), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        log.error("Generic unhandled exception", ex);
        return handleExceptionInternal(ex, new ErrorMessageDTO(UNEXPECTED_ERROR), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ErrorMessageDTO createError(ObjectError error, Class<?> dtoClass) {
        String field = "";
        if (error instanceof FieldError fieldError) {
            field = fieldError.getField();

            try {
                Field dtoField = dtoClass.getDeclaredField(field);

                JsonProperty jsonProperty = dtoField.getAnnotation(JsonProperty.class);
                if (jsonProperty != null) {
                    field = jsonProperty.value();
                }
            } catch (NoSuchFieldException e) {
                log.error(UNEXPECTED_ERROR);
            }
        }

        return new ErrorMessageDTO(error.getDefaultMessage(), field);
    }

}
