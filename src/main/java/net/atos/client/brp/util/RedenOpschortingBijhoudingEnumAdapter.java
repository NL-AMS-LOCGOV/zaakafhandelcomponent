/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.RedenOpschortingBijhoudingEnum;

public class RedenOpschortingBijhoudingEnumAdapter implements JsonbAdapter<RedenOpschortingBijhoudingEnum, String> {

    @Override
    public String adaptToJson(final RedenOpschortingBijhoudingEnum redenOpschortingBijhoudingEnum) {
        return redenOpschortingBijhoudingEnum.toString();
    }

    @Override
    public RedenOpschortingBijhoudingEnum adaptFromJson(final String json) {
        return RedenOpschortingBijhoudingEnum.fromValue(json);
    }
}
