/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.BooleanUtils;

import net.atos.client.zgw.shared.model.AbstractEnum;

public class RESTHistorieRegel {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm").withZone(ZoneId.systemDefault());

    private static final String TRUE = "Ja";

    private static final String FALSE = "Nee";

    public final String attribuutLabel;

    public final String oudeWaarde;

    public final String nieuweWaarde;

    public ZonedDateTime datumTijd;

    public String door;

    public String applicatie;

    public String toelichting;

    public RESTHistorieRegel(final String attribuutLabel, final String oudeWaarde, final String nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = oudeWaarde;
        this.nieuweWaarde = nieuweWaarde;
    }

    public RESTHistorieRegel(final String attribuutLabel, final LocalDate oudeWaarde, final LocalDate nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = toWaarde(oudeWaarde);
        this.nieuweWaarde = toWaarde(nieuweWaarde);
    }

    public RESTHistorieRegel(final String attribuutLabel, final Boolean oudeWaarde, final Boolean nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = toWaarde(oudeWaarde);
        this.nieuweWaarde = toWaarde(nieuweWaarde);
    }

    public RESTHistorieRegel(final String attribuutLabel, final AbstractEnum<?> oudeWaarde, final AbstractEnum<?> nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = toWaarde(oudeWaarde);
        this.nieuweWaarde = toWaarde(nieuweWaarde);
    }

    public RESTHistorieRegel(final String attribuutLabel, final ZonedDateTime oudeWaarde, final ZonedDateTime nieuweWaarde) {
        this.attribuutLabel = attribuutLabel;
        this.oudeWaarde = toWaarde(oudeWaarde);
        this.nieuweWaarde = toWaarde(nieuweWaarde);
    }

    private String toWaarde(final LocalDate date) {
        return date != null ? DATE_FORMATTER.format(date) : null;
    }

    private String toWaarde(final ZonedDateTime date) {
        return date != null ? DATE_TIME_FORMATTER.format(date) : null;
    }

    private String toWaarde(final AbstractEnum<?> abstractEnum) {
        return abstractEnum != null ? abstractEnum.toValue() : null;
    }

    private String toWaarde(final Boolean bool) {
        return bool != null ? BooleanUtils.toString(bool, TRUE, FALSE) : null;
    }
}
