package com.fullcycle.admin.catalogo.domain.validation;

import java.util.List;

public interface ValidationHandler {

    ValidationHandler append(Error anError);
    ValidationHandler append(ValidationHandler aHandler);
    ValidationHandler validate(Validation aValidation);

    List<Error> getErrors();

    default boolean hasError() {
        var error = getErrors();
        return error != null && !error.isEmpty();
    }

    default Error firstError() {
        if (hasError()) {
            return getErrors().get(0);
        }
        return null;
    }

    interface Validation {
        void validate();
    }
}
