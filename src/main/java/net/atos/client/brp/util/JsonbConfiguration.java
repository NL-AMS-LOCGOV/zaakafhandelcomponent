/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.ext.ContextResolver;

public class JsonbConfiguration implements ContextResolver<Jsonb> {

    private Jsonb jsonb;

    public JsonbConfiguration() {
        final JsonbConfig jsonbConfig = new JsonbConfig()
                .withDeserializers(new PersonenQueryResponseJsonbDeserializer());
        jsonb = JsonbBuilder.create(jsonbConfig);
    }

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}
