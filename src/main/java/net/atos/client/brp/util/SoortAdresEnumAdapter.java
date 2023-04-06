/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.SoortAdresEnum;

public class SoortAdresEnumAdapter implements JsonbAdapter<SoortAdresEnum, String> {

    @Override
    public String adaptToJson(final SoortAdresEnum soortAdresEnum) {
        return soortAdresEnum.toString();
    }

    @Override
    public SoortAdresEnum adaptFromJson(final String json) {
        return SoortAdresEnum.fromValue(json);
    }
}
