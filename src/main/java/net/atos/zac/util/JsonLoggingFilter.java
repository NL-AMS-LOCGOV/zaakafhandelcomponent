/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static java.util.logging.Level.FINE;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

/**
 *
 */
@Provider
public class JsonLoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Level LOG_LEVEL = FINE;

    private static final Logger LOG = Logger.getLogger(JsonLoggingFilter.class.getName());

    @Override
    public void filter(final ClientRequestContext requestContext) {
        if (LOG.isLoggable(LOG_LEVEL)) {
            final StringBuilder message = new StringBuilder("REST Request\n");
            message.append(String.format("URL: %s\n", requestContext.getUri().toString()));
            message.append(String.format("Method: %s\n", requestContext.getMethod()));
            message.append(String.format("Media type: %s\n", requestContext.getMediaType()));
            message.append("Headers:\n");
            requestContext.getHeaders().forEach((header, value) -> message.append(String.format("   %s : %s\n", header, value)));
            if (requestContext.hasEntity()) {
                message.append("Payload:");
                message.append(getPayload(requestContext));
            }
            LOG.log(LOG_LEVEL, message.toString());
        }
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) {
        if (LOG.isLoggable(LOG_LEVEL)) {
            final StringBuilder message = new StringBuilder("REST Response\n");
            message.append(
                    String.format("Status: %d (%s)\n", responseContext.getStatusInfo().getStatusCode(), responseContext.getStatusInfo().getReasonPhrase()));
            message.append(String.format("Media type: %s\n", responseContext.getMediaType()));
            message.append("Headers:\n");
            responseContext.getHeaders().forEach((header, value) -> message.append(String.format("   %s : %s\n", header, value)));
            if (responseContext.hasEntity()) {
                message.append("Payload:");
                message.append(getPayload(responseContext));
            }
            LOG.log(LOG_LEVEL, message.toString());
        }
    }

    private String getPayload(final ClientRequestContext requestContext) {
        final JsonbConfig jsonbConfig = new JsonbConfig();
        jsonbConfig.setProperty(JsonbConfig.FORMATTING, true);
        return JsonbBuilder.create(jsonbConfig).toJson(requestContext.getEntity());
    }

    private String getPayload(final ClientResponseContext responseContext) {
        try {
            final String payload = IOUtils.toString(responseContext.getEntityStream(), StandardCharsets.UTF_8);
            responseContext.setEntityStream(IOUtils.toInputStream(payload, StandardCharsets.UTF_8));
            try {
                final Map<String, Object> jsonConfig = new HashMap<>();
                jsonConfig.put(JsonGenerator.PRETTY_PRINTING, true);
                final StringWriter payloadWriter = new StringWriter();
                Json.createWriterFactory(jsonConfig)
                        .createWriter(payloadWriter)
                        .write(Json.createReader(new StringReader(payload)).read());
                return payloadWriter.toString();
            } catch (final JsonParsingException ignore) {
                return payload;
            }
        } catch (final IOException e) {
            throw new RuntimeException("Fout tijdens loggen van REST response", e);
        }
    }
}
