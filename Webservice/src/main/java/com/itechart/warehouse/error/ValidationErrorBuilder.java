package com.itechart.warehouse.error;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

/**
 * Builds error to be returned to the client.
 */
public class ValidationErrorBuilder {

    public static ValidationError fromBindingErrors(Errors errors) {
        ValidationError error = new ValidationError("Validation failed. " + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addValidationError(objectError.getDefaultMessage());
        }
        return error;
    }
}
