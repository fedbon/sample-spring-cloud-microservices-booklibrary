package ru.fedbon.userserver.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import ru.fedbon.userserver.exception.ApiException;
import ru.fedbon.userserver.exception.InvalidCredentialsException;
import ru.fedbon.userserver.exception.NotFoundException;
import ru.fedbon.userserver.exception.PasswordEncoderException;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({ApiException.class, InvalidCredentialsException.class, PasswordEncoderException.class})
    public ResponseEntity<Object> handleApiException(ApiException ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(), "UNAUTHORIZED"));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(ApiException ex) {
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage(),
                HttpStatus.NOT_FOUND.value(), "NOT_FOUND"));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServerError(Exception ex) {
        var errorMessage = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getName();
        return ResponseEntity.internalServerError().body(buildErrorResponse(errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_SERVER_ERROR"));
    }

    private Map<String, Object> buildErrorResponse(String message, int status, String errorCode) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();

        List<Map<String, Object>> errorList = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put("message", message);
        errorMap.put("status", status);
        errorMap.put("errorCode", errorCode);
        errorList.add(errorMap);

        errorAttributes.put("errors", errorList);

        return errorAttributes;
    }
}
