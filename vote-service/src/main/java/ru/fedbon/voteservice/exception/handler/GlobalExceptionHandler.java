package ru.fedbon.voteservice.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import ru.fedbon.voteservice.exception.ApiException;
import ru.fedbon.voteservice.exception.NotFoundException;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.fedbon.voteservice.constants.AppConstants.ERRORS;
import static ru.fedbon.voteservice.constants.AppConstants.ERROR_CODE;
import static ru.fedbon.voteservice.constants.AppConstants.MESSAGE;
import static ru.fedbon.voteservice.constants.AppConstants.STATUS;
import static ru.fedbon.voteservice.constants.ErrorMessage.INTERNAL_SERVER_ERROR;
import static ru.fedbon.voteservice.constants.ErrorMessage.NOT_FOUND;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ApiException.class, NotFoundException.class})
    public ResponseEntity<Object> handleApiException(ApiException ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage(),
                HttpStatus.NOT_FOUND.value(), NOT_FOUND));
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServerError(Exception ex) {
        var errorMessage = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getName();
        return ResponseEntity.internalServerError().body(buildErrorResponse(errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR));
    }

    private Map<String, Object> buildErrorResponse(String message, int status, String errorCode) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();

        List<Map<String, Object>> errorList = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(MESSAGE, message);
        errorMap.put(STATUS, status);
        errorMap.put(ERROR_CODE, errorCode);
        errorList.add(errorMap);

        errorAttributes.put(ERRORS, errorList);

        return errorAttributes;
    }
}
