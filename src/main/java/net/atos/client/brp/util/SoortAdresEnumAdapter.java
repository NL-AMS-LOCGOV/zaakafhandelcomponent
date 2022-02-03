/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.SoortAdresEnum;

public class SoortAdresEnumAdapter implements JsonbAdapter<SoortAdresEnum, String> {

    @Override
    public String adaptToJson(final SoortAdresEnum soortAdresEnum) throws Exception {
        return soortAdresEnum.toString();
    }

    @Override
    public SoortAdresEnum adaptFromJson(final String json) throws Exception {
        return SoortAdresEnum.fromValue(json);
    }
}
