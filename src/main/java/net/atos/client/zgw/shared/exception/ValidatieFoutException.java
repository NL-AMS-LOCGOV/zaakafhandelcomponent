/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.exception;

import java.net.URI;
import java.util.stream.Collectors;

import net.atos.client.zgw.shared.model.ValidatieFout;

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
        return "%s [%d %s] %s: %s (%s %s)"
                .formatted(validatieFout.getTitle(),
                           validatieFout.getStatus(),
                           validatieFout.getCode(),
                           validatieFout.getDetail(),
                           validatieFout.getInvalidParams().stream()
                                   .map(error -> "%s [%s] %s"
                                           .formatted(error.getName(),
                                                      error.getCode(),
                                                      error.getReason()))
                                   .collect(Collectors.joining(", ")),
                           uri(validatieFout.getType()),
                           uri(validatieFout.getInstance()));
    }

    private String uri(final URI uri) {
        return uri == null ? null : uri.toString();
    }
}
