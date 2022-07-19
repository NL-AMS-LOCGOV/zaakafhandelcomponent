/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.exception;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

/**
 *
 */
public class RuntimeExceptionMapper implements ResponseExceptionMapper<RuntimeException> {


    @Override
    public boolean handles(final int status, final MultivaluedMap<String, Object> headers) {
        return status >= Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    @Override
    public RuntimeException toThrowable(final Response response) {
        return new RuntimeException(String.format("Server response from BAG bevragen: %d (%s)", response.getStatus(), response.getStatusInfo()));
    }
}
