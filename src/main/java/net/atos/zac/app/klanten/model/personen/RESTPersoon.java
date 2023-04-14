/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.personen;

import static net.atos.zac.app.klanten.model.klant.IdentificatieType.BSN;

import net.atos.zac.app.klanten.model.klant.IdentificatieType;
import net.atos.zac.app.klanten.model.klant.RESTKlant;

public class RESTPersoon extends RESTKlant {

    public String bsn;

    public String geslacht;

    public String naam;

    public String geboortedatum;

    public String verblijfplaats;

    public RESTPersoon() {
    }

    public RESTPersoon(final String naam, final String geboortedatum, final String verblijfplaats) {
        this.naam = naam;
        this.geboortedatum = geboortedatum;
        this.verblijfplaats = verblijfplaats;
    }

    @Override
    public IdentificatieType getIdentificatieType() {
        return bsn != null ? BSN : null;
    }

    @Override
    public String getIdentificatie() {
        return bsn;
    }

    @Override
    public String getNaam() {
        return naam;
    }
}
