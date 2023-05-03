/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.StatusVerblijfsobject;

public class StatusVerblijfsobjectEnumAdapter implements JsonbAdapter<StatusVerblijfsobject, String> {

    @Override
    public String adaptToJson(final StatusVerblijfsobject statusVerblijfsobject) {
        return statusVerblijfsobject.toString();
    }

    @Override
    public StatusVerblijfsobject adaptFromJson(final String json) {
        return StatusVerblijfsobject.fromValue(json);
    }
}
