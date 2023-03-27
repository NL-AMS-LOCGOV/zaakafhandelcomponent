/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.NaamgebruikEnum;

public class NaamgebruikEnumAdapter implements JsonbAdapter<NaamgebruikEnum, String> {

    @Override
    public String adaptToJson(final NaamgebruikEnum naamgebruikEnum) {
        return naamgebruikEnum.toString();
    }

    @Override
    public NaamgebruikEnum adaptFromJson(final String json) {
        return NaamgebruikEnum.fromValue(json);
    }
}
