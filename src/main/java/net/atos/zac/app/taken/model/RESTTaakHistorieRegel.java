/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import net.atos.client.zgw.shared.util.HistorieUtil;

public class RESTTaakHistorieRegel {

    public final String attribuutLabel;

    public final String oudeWaarde;

    public final String nieuweWaarde;

    public final String toelichting;

    public ZonedDateTime datumTijd;

    public RESTTaakHistorieRegel(final String attribuutLabel, final String oudeWaarde, final String nieuweWaarde,
            final String toelichting) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = oudeWaarde;
        this.nieuweWaarde = nieuweWaarde;
        this.toelichting = toelichting;
    }

    public RESTTaakHistorieRegel(final String attribuutLabel) {
        this(attribuutLabel, (String) null, null, null);
    }

    public RESTTaakHistorieRegel(final String attribuutLabel, final LocalDate oudeWaarde,
            final LocalDate nieuweWaarde, final String toelichting) {
        this(attribuutLabel, HistorieUtil.toWaarde(oudeWaarde), HistorieUtil.toWaarde(nieuweWaarde), toelichting);
    }
}
