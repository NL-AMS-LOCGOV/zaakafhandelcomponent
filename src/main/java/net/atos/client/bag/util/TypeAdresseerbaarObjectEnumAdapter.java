/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.TypeAdresseerbaarObject;

public class TypeAdresseerbaarObjectEnumAdapter implements JsonbAdapter<TypeAdresseerbaarObject, String> {

    @Override
    public String adaptToJson(final TypeAdresseerbaarObject typeAdresseerbaarObject) throws Exception {
        return typeAdresseerbaarObject.toString();
    }

    @Override
    public TypeAdresseerbaarObject adaptFromJson(final String json) {
        return TypeAdresseerbaarObject.fromValue(json);
    }
}
