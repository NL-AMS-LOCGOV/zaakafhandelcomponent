/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.exception;

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
        return validatieFout.toString();
    }
}
