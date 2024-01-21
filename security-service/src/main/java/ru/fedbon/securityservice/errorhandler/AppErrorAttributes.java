package ru.fedbon.securityservice.errorhandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import ru.fedbon.securityservice.exception.ApiException;
import ru.fedbon.securityservice.exception.AuthException;
import ru.fedbon.securityservice.exception.UnauthorizedException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class AppErrorAttributes extends DefaultErrorAttributes {
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public AppErrorAttributes() {
        super();
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        var error = getError(request);

        var errorList = new ArrayList<Map<String, Object>>();
        determineHttpStatusAndBuildErrorList(error, errorList);

        var errors = new HashMap<String, Object>();
        errors.put("errors", errorList);
        errorAttributes.put("status", status.value());
        errorAttributes.put("errors", errors);

        return errorAttributes;
    }

    private void determineHttpStatusAndBuildErrorList(Throwable error, List<Map<String, Object>> errorList) {
        if (error instanceof ApiException apiException) {
            if (isUnauthorizedError(apiException)) {
                handleUnauthorizedError(apiException, errorList);
            } else {
                handleApiException(apiException, errorList);
            }
        } else if (error instanceof ExpiredJwtException expiredJwtException) {
            handleExpiredJwtException(expiredJwtException, errorList);
        } else {
            handleInternalServerError(error, errorList);
        }
    }

    private boolean isUnauthorizedError(Throwable error) {
        return error instanceof AuthException || error instanceof UnauthorizedException
                || error instanceof ExpiredJwtException || error instanceof SecurityException
                || error instanceof MalformedJwtException;
    }

    private void handleExpiredJwtException(ExpiredJwtException expiredJwtException,
                                           List<Map<String, Object>> errorList) {
        status = HttpStatus.UNAUTHORIZED;
        var errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("code", "TOKEN_EXPIRED");
        errorMap.put("message", expiredJwtException.getMessage());
        errorList.add(errorMap);
    }

    private void handleUnauthorizedError(ApiException apiException, List<Map<String, Object>> errorList) {
        status = HttpStatus.UNAUTHORIZED;
        addErrorToErrorList(apiException, errorList);
    }

    private void handleApiException(ApiException apiException, List<Map<String, Object>> errorList) {
        status = HttpStatus.BAD_REQUEST;
        addErrorToErrorList(apiException, errorList);
    }

    private void handleInternalServerError(Throwable error, List<Map<String, Object>> errorList) {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = (error.getMessage() != null) ? error.getMessage() : error.getClass().getName();
        var errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("code", "INTERNAL_ERROR");
        errorMap.put("message", message);
        errorList.add(errorMap);
    }

    private void addErrorToErrorList(ApiException apiException, List<Map<String, Object>> errorList) {
        var errorMap = new LinkedHashMap<String, Object>();
        errorMap.put("code", apiException.getErrorCode());
        errorMap.put("message", apiException.getMessage());
        errorList.add(errorMap);
    }
}

