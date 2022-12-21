/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.contactmomenten.converter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.contactmomenten.model.ContactMoment;
import net.atos.client.contactmomenten.model.Medewerker;
import net.atos.zac.app.contactmomenten.model.RESTContactmoment;

public class ContactmomentConverter {

    public RESTContactmoment convert(final ContactMoment contactMoment) {
        final var restContactmoment = new RESTContactmoment();
        if (contactMoment.getRegistratiedatum() != null) {
            restContactmoment.registratiedatum = contactMoment.getRegistratiedatum().toZonedDateTime();
        }
        if (contactMoment.getInitiatiefnemer() != null) {
            restContactmoment.initiatiefnemer = contactMoment.getInitiatiefnemer().value();
        }
        restContactmoment.kanaal = contactMoment.getKanaal();
        restContactmoment.tekst = contactMoment.getTekst();
        if (contactMoment.getMedewerkerIdentificatie() != null) {
            restContactmoment.medewerker = convert(contactMoment.getMedewerkerIdentificatie());
        }
        return restContactmoment;
    }

    private String convert(final Medewerker medewerker) {
        if (isNotBlank(medewerker.getAchternaam())) {
            final StringBuilder naam = new StringBuilder();
            if (isNotBlank(medewerker.getVoorletters())) {
                naam.append(medewerker.getVoorletters());
                naam.append(StringUtils.SPACE);
            }
            if (isNotBlank(medewerker.getVoorvoegselAchternaam())) {
                naam.append(medewerker.getVoorvoegselAchternaam());
                naam.append(StringUtils.SPACE);
            }
            naam.append(medewerker.getAchternaam());
            return naam.toString();
        } else {
            return medewerker.getIdentificatie();
        }
    }
}
