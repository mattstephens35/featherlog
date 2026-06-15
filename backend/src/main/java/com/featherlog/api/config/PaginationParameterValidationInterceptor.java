package com.featherlog.api.config;

import com.featherlog.api.exception.InvalidPaginationParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class PaginationParameterValidationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        validateNonNegativeInteger(request, "page");
        validateNonNegativeInteger(request, "size");
        return true;
    }

    private void validateNonNegativeInteger(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        if (value == null) {
            return;
        }

        try {
            if (Integer.parseInt(value) < 0) {
                throw new InvalidPaginationParameterException("Invalid value '%s' for parameter '%s'".formatted(value, parameterName));
            }
        } catch (NumberFormatException ex) {
            throw new InvalidPaginationParameterException("Invalid value '%s' for parameter '%s'".formatted(value, parameterName));
        }
    }
}
