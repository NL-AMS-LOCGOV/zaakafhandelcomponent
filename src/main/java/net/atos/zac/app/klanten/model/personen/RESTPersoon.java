/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.model.personen;

import net.atos.zac.app.klanten.model.KlantType;
import net.atos.zac.app.klanten.model.RESTKlant;

public class RESTPersoon extends RESTKlant {

    public String bsn;

    public String geslacht;

    public String naam;

    public String geboortedatum;

    public String inschrijfadres;

    @Override
    public String getIdentificatie() {
        return bsn;
    }

    @Override
    public KlantType getKlantType() {
        return KlantType.PERSOON;
    }

    @Override
    public String getNaam() {
        return naam;
    }
}
