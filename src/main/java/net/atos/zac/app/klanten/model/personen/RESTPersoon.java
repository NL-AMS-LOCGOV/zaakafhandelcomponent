/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.personen;

import static net.atos.zac.app.klanten.model.IdentificatieType.BSN;

import net.atos.zac.app.klanten.model.IdentificatieType;
import net.atos.zac.app.klanten.model.RESTKlant;

public class RESTPersoon extends RESTKlant {

    public String bsn;

    public String geslacht;

    public String naam;

    public String geboortedatum;

    public String inschrijfadres;

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
