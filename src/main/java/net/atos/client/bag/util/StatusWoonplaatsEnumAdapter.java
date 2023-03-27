/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.StatusWoonplaats;

public class StatusWoonplaatsEnumAdapter implements JsonbAdapter<StatusWoonplaats, String> {

    @Override
    public String adaptToJson(final StatusWoonplaats statusWoonplaats) {
        return statusWoonplaats.toString();
    }

    @Override
    public StatusWoonplaats adaptFromJson(final String json) {
        return StatusWoonplaats.fromValue(json);
    }
}
