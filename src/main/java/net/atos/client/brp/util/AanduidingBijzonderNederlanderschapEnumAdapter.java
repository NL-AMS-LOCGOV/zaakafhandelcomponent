/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.AanduidingBijzonderNederlanderschapEnum;

public class AanduidingBijzonderNederlanderschapEnumAdapter implements JsonbAdapter<AanduidingBijzonderNederlanderschapEnum, String> {

    @Override
    public String adaptToJson(final AanduidingBijzonderNederlanderschapEnum aanduidingBijzonderNederlanderschapEnum) throws Exception {
        return aanduidingBijzonderNederlanderschapEnum.toString();
    }

    @Override
    public AanduidingBijzonderNederlanderschapEnum adaptFromJson(final String json) throws Exception {
        return AanduidingBijzonderNederlanderschapEnum.fromValue(json);
    }
}
