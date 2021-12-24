/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.shared.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class ValidatieFout extends Fout {

    @JsonbProperty("invalid_params")
    private List<FieldValidationError> fieldValidationErrors;

    public List<FieldValidationError> getFieldValidationErrors() {
        return fieldValidationErrors;
    }

    public void setFieldValidationErrors(final List<FieldValidationError> fieldValidationErrors) {
        this.fieldValidationErrors = fieldValidationErrors;
    }
}
