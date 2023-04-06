/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.GeslachtEnum;

public class GeslachtEnumAdapter implements JsonbAdapter<GeslachtEnum, String> {

    @Override
    public String adaptToJson(final GeslachtEnum geslachtEnum) {
        return geslachtEnum.toString();
    }

    @Override
    public GeslachtEnum adaptFromJson(final String json) {
        return GeslachtEnum.fromValue(json);
    }
}
