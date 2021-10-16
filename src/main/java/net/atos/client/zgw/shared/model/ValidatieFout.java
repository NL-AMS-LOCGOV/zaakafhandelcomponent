/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import java.net.URI;
import java.util.List;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class ValidatieFout extends Fout {

    private final List<FieldValidationError> invalidParams;

    @JsonbCreator
    public ValidatieFout(@JsonbProperty("type") final URI type,
            @JsonbProperty("code") final String code,
            @JsonbProperty("title") final String title,
            @JsonbProperty("status") final int status,
            @JsonbProperty("detail") final String detail,
            @JsonbProperty("instance") final URI instance,
            @JsonbProperty("invalidParams") final List<FieldValidationError> invalidParams) {
        super(type, code, title, status, detail, instance);
        this.invalidParams = invalidParams;
    }

    public List<FieldValidationError> getInvalidParams() {
        return invalidParams;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString()).append("\n");
        invalidParams.forEach(fieldValidationError -> stringBuilder.append(fieldValidationError.toString()).append("\n"));
        return stringBuilder.toString();
    }
}
