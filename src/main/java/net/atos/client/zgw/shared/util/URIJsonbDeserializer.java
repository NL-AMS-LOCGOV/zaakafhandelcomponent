/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.util;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.apache.commons.lang3.StringUtils;

public class URIJsonbDeserializer implements JsonbDeserializer<URI> {

    @Override
    public URI deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
        try {
            final String uri = parser.getString();
            return StringUtils.isNotEmpty(uri) ? new URI(uri) : null;
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
