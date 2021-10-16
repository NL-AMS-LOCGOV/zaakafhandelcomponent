/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.util.logging.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 *
 */
@Provider
public class LoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void filter(final ClientRequestContext requestContext) {
        // ToDo: ESUITEDEV-22996
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) {
        // ToDo: ESUITEDEV-22996
    }
}
