/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.shared.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


/**
 * Exceptionmapper om {@link FoutmeldingException} die gegooit zijn op te vangen en om te zetten
 * naar een {@link Response} welke teruggeven wordt naar de frontend voor verdere afwerking.
 */
@Provider
public class FoutMeldingExceptionMapper implements ExceptionMapper<FoutmeldingException> {

    @Override
    public Response toResponse(final FoutmeldingException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
}
