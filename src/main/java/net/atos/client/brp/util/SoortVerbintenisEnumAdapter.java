/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.SoortVerbintenisEnum;

public class SoortVerbintenisEnumAdapter implements JsonbAdapter<SoortVerbintenisEnum, String> {

    @Override
    public String adaptToJson(final SoortVerbintenisEnum soortVerbintenisEnum) throws Exception {
        return soortVerbintenisEnum.toString();
    }

    @Override
    public SoortVerbintenisEnum adaptFromJson(final String json) throws Exception {
        return SoortVerbintenisEnum.fromValue(json);
    }
}
