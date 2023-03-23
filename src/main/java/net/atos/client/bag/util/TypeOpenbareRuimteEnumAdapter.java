/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.TypeOpenbareRuimte;

public class TypeOpenbareRuimteEnumAdapter implements JsonbAdapter<TypeOpenbareRuimte, String> {

    @Override
    public String adaptToJson(final TypeOpenbareRuimte typeOpenbareRuimte) throws Exception {
        return typeOpenbareRuimte.toString();
    }

    @Override
    public TypeOpenbareRuimte adaptFromJson(final String json) {
        return TypeOpenbareRuimte.fromValue(json);
    }
}
