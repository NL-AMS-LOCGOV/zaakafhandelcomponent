/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.shared.exception;

import java.net.URI;
import java.util.stream.Collectors;

import net.atos.client.or.shared.model.ValidatieFout;

/**
 *
 */
public class ValidatieFoutException extends RuntimeException {

    private final ValidatieFout validatieFout;

    public ValidatieFoutException(final ValidatieFout validatieFout) {
        this.validatieFout = validatieFout;
    }

    public ValidatieFout getValidatieFout() {
        return validatieFout;
    }

    @Override
    public String getMessage() {
        return "[%d] %s %s %s %s: %s"
                .formatted(validatieFout.getStatus(),
                           validatieFout.getCode(),
                           validatieFout.getTitle(),
                           validatieFout.getDetail(),
                           uri(validatieFout.getInstance()),
                           validatieFout.getFieldValidationErrors().stream()
                                   .map(error -> "%s %s %s"
                                           .formatted(error.getCode(),
                                                      error.getName(),
                                                      error.getReason()))
                                   .collect(Collectors.joining(", ")));
    }

    private String uri(final URI uri) {
        return uri == null ? null : uri.toString();
    }
}
