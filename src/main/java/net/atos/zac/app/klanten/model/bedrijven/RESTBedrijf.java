/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.bedrijven;

import static net.atos.zac.app.klanten.model.klant.IdentificatieType.RSIN;
import static net.atos.zac.app.klanten.model.klant.IdentificatieType.VN;

import net.atos.zac.app.klanten.model.klant.IdentificatieType;
import net.atos.zac.app.klanten.model.klant.RESTKlant;

public class RESTBedrijf extends RESTKlant {

    public String vestigingsnummer;

    public String kvkNummer;

    public String handelsnaam;

    public String rsin;

    public String adres;

    public String postcode;

    public String type;

    @Override
    public IdentificatieType getIdentificatieType() {
        return vestigingsnummer != null ? VN : rsin != null ? RSIN : null;
    }

    @Override
    public String getIdentificatie() {
        return vestigingsnummer != null ? vestigingsnummer : rsin;
    }

    @Override
    public String getNaam() {
        return handelsnaam;
    }
}
