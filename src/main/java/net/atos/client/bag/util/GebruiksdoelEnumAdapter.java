/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.Gebruiksdoel;

public class GebruiksdoelEnumAdapter implements JsonbAdapter<Gebruiksdoel, String> {

    @Override
    public String adaptToJson(final Gebruiksdoel gebruiksdoel) {
        return gebruiksdoel.toString();
    }

    @Override
    public Gebruiksdoel adaptFromJson(final String json) {
        return Gebruiksdoel.fromValue(json);
    }
}
