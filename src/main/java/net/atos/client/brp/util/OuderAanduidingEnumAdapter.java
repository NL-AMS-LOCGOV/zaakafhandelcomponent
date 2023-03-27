/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.OuderAanduidingEnum;

public class OuderAanduidingEnumAdapter implements JsonbAdapter<OuderAanduidingEnum, String> {

    @Override
    public String adaptToJson(final OuderAanduidingEnum ouderAanduidingEnum) {
        return ouderAanduidingEnum.toString();
    }

    @Override
    public OuderAanduidingEnum adaptFromJson(final String json) {
        return OuderAanduidingEnum.fromValue(json);
    }
}
