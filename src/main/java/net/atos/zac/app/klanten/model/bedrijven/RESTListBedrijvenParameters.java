/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.bedrijven;

import org.apache.commons.lang3.StringUtils;

public class RESTListBedrijvenParameters {

    public String kvkNummer;

    public String vestigingsnummer;

    public String handelsnaam;

    public String postcode;

    public Integer huisnummer;

    public BedrijfType type;

    public boolean isValid() {
        return StringUtils.isNotBlank(kvkNummer) ||
                StringUtils.isNotBlank(vestigingsnummer) ||
                StringUtils.isNotBlank(handelsnaam) ||
                StringUtils.isNotBlank(postcode) && huisnummer != null && huisnummer > 0;
    }
}
