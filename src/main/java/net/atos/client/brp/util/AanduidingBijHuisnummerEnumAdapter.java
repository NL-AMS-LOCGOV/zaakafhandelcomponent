/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.AanduidingBijHuisnummerEnum;

public class AanduidingBijHuisnummerEnumAdapter implements JsonbAdapter<AanduidingBijHuisnummerEnum, String> {

    @Override
    public String adaptToJson(final AanduidingBijHuisnummerEnum aanduidingBijHuisnummerEnum) {
        return aanduidingBijHuisnummerEnum.toString();
    }

    @Override
    public AanduidingBijHuisnummerEnum adaptFromJson(final String json) {
        return AanduidingBijHuisnummerEnum.fromValue(json);
    }
}
