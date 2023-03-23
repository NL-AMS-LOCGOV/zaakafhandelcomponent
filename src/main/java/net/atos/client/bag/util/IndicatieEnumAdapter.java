/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.Indicatie;

public class IndicatieEnumAdapter implements JsonbAdapter<Indicatie, String> {

    @Override
    public String adaptToJson(final Indicatie indicatie) throws Exception {
        return indicatie.toString();
    }

    @Override
    public Indicatie adaptFromJson(final String json) {
        return Indicatie.fromValue(json);
    }
}
