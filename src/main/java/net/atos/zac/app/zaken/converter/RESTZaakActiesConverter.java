/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import net.atos.zac.app.zaken.model.RESTZaakActies;
import net.atos.zac.policy.output.ZaakActies;

public class RESTZaakActiesConverter {

    public RESTZaakActies convert(final ZaakActies zaakActies) {
        final RESTZaakActies restZaakActies = new RESTZaakActies();

        restZaakActies.lezen = zaakActies.getLezen();
        restZaakActies.opschorten = zaakActies.getOpschorten();
        restZaakActies.verlengen = zaakActies.getVerlengen();
        restZaakActies.hervatten = zaakActies.getHervatten();
        restZaakActies.afbreken = zaakActies.getAfbreken();
        restZaakActies.afsluiten = zaakActies.getAfsluiten();
        restZaakActies.heropenen = zaakActies.getHeropenen();
        restZaakActies.creeerenDocument = zaakActies.getCreeerenDocument();
        restZaakActies.toevoegenDocument = zaakActies.getToevoegenDocument();
        restZaakActies.koppelen = zaakActies.getKoppelen();
        restZaakActies.versturenEmail = zaakActies.getVersturenEmail();
        restZaakActies.versturenOntvangstbevestiging = zaakActies.getVersturenOntvangstbevestiging();
        restZaakActies.toevoegenPersoon = zaakActies.getToevoegenPersoon();
        restZaakActies.toevoegenBedrijf = zaakActies.getToevoegenBedrijf();
        restZaakActies.verwijderenInitiator = zaakActies.getVerwijderenInitiator();
        restZaakActies.wijzigenToekenning = zaakActies.getWijzigenToekenning();
        restZaakActies.wijzigenOverig = zaakActies.getWijzigenOverig();
        restZaakActies.startenPlanItems = zaakActies.getStartenPlanItems();

        return restZaakActies;
    }
}
