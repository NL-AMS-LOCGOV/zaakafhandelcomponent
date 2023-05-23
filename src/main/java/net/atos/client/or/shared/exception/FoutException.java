/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.shared.exception;

import java.net.URI;

import net.atos.client.or.shared.model.Fout;

/**
 *
 */
public class FoutException extends RuntimeException {

    private final Fout fout;

    public FoutException(final Fout fout) {
        this.fout = fout;
    }

    public Fout getFout() {
        return fout;
    }

    @Override
    public String getMessage() {
        return "[%d %s] %s %s (%s)"
                .formatted(fout.getStatus(),
                           fout.getCode(),
                           fout.getTitle(),
                           fout.getDetail(),
                           uri(fout.getInstance()));
    }

    private String uri(final URI uri) {
        return uri == null ? null : uri.toString();
    }
}
