/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.personen;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;

public class RESTListPersonenParameters {

    public String bsn;

    public String geslachtsnaam;

    public LocalDate geboortedatum;

    public String gemeenteVanInschrijving;

    public String postcode;

    public Integer huisnummer;

    public boolean isValid() {
        return StringUtils.isNotBlank(bsn) ||
                geboortedatum != null && StringUtils.isNotBlank(geslachtsnaam) ||
                StringUtils.isNotBlank(geslachtsnaam) && StringUtils.isNotBlank(gemeenteVanInschrijving) ||
                StringUtils.isNotBlank(postcode) && huisnummer != null && huisnummer > 0;
    }
}
