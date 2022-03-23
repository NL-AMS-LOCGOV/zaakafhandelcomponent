/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.bedrijven;

import net.atos.zac.app.klanten.model.KlantType;
import net.atos.zac.app.klanten.model.RESTKlant;

public class RESTBedrijf extends RESTKlant {

    public String vestigingsnummer;

    public String kvkNummer;

    public String handelsnaam;

    public String rsin;

    public String adres;

    public String postcode;

    public String type;

    @Override
    public String getIdentificatie() {
        return vestigingsnummer;
    }

    @Override
    public KlantType getKlantType() {
        return KlantType.BEDRIJF;
    }

    @Override
    public String getNaam() {
        return handelsnaam;
    }
}
