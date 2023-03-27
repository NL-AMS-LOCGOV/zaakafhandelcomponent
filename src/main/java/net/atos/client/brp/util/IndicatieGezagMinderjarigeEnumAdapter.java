/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.brp.util;

import javax.json.bind.adapter.JsonbAdapter;

import net.atos.client.brp.model.IndicatieGezagMinderjarigeEnum;

public class IndicatieGezagMinderjarigeEnumAdapter implements JsonbAdapter<IndicatieGezagMinderjarigeEnum, String> {

    @Override
    public String adaptToJson(final IndicatieGezagMinderjarigeEnum indicatieGezagMinderjarigeEnum) {
        return indicatieGezagMinderjarigeEnum.toString();
    }

    @Override
    public IndicatieGezagMinderjarigeEnum adaptFromJson(final String json) {
        return IndicatieGezagMinderjarigeEnum.fromValue(json);
    }
}
