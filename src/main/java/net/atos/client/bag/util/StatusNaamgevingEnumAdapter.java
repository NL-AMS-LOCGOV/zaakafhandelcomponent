/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.bag.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.bag.model.StatusNaamgeving;

public class StatusNaamgevingEnumAdapter implements JsonbAdapter<StatusNaamgeving, String> {

    @Override
    public String adaptToJson(final StatusNaamgeving statusNaamgeving) throws Exception {
        return statusNaamgeving.toString();
    }

    @Override
    public StatusNaamgeving adaptFromJson(final String json) {
        return StatusNaamgeving.fromValue(json);
    }
}
