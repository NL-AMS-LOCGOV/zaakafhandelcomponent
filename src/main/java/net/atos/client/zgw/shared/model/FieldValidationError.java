/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import static java.lang.String.format;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 */
public class FieldValidationError {

    // Naam van het veld met ongeldige gegevens
    private final String name;

    // Systeemcode die het type fout aangeeft
    private final String code;

    // Uitleg wat er precies fout is met de gegevens
    private final String reason;

    @JsonbCreator
    public FieldValidationError(@JsonbProperty("name") final String name,
            @JsonbProperty("code") final String code,
            @JsonbProperty("reason") final String reason) {
        this.name = name;
        this.code = code;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return format("Name: %s, Code: %s, Reason: %s", name, code, reason);
    }
}
