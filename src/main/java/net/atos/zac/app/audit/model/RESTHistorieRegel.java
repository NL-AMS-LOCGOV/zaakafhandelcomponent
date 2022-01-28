/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.model;

import java.time.ZonedDateTime;

public class RESTHistorieRegel {

    public RESTHistorieRegel(final String attribuutLabel) {
        this.attribuutLabel = attribuutLabel;
    }

    public RESTHistorieRegel(final String attribuutLabel, final Object oudeWaarde, final Object nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        if (oudeWaarde != null) {
            this.oudeWaarde = oudeWaarde.toString();
        }
        if (nieuweWaarde != null) {
            this.nieuweWaarde = nieuweWaarde.toString();
        }
    }

    public ZonedDateTime datumTijd;

    public String door;

    public String applicatie;

    public String attribuutLabel;

    public String oudeWaarde;

    public String nieuweWaarde;

    public String toelichting;
}
