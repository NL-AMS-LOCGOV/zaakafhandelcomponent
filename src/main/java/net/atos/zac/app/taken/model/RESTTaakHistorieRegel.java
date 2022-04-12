/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.model;

import java.time.ZonedDateTime;

public class RESTTaakHistorieRegel {

    public RESTTaakHistorieRegel(final String attribuutLabel) {
        this.attribuutLabel = attribuutLabel;
    }

    public RESTTaakHistorieRegel(final String attribuutLabel, final String oudeWaarde, final String nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = oudeWaarde;
        this.nieuweWaarde = nieuweWaarde;
    }

    public ZonedDateTime datumTijd;

    public String attribuutLabel;

    public String oudeWaarde;

    public String nieuweWaarde;
}
