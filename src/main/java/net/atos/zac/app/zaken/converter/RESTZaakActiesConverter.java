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

        restZaakActies.opschorten = zaakActies.getOpschorten();
        restZaakActies.verlengen = zaakActies.getVerlengen();
        restZaakActies.hervatten = zaakActies.getHervatten();
        restZaakActies.afbreken = zaakActies.getAfbreken();
        restZaakActies.afsluiten = zaakActies.getAfsluiten(); // ToDo: Alle deelzaken moeten geslotenm zijn.
        restZaakActies.heropenen = zaakActies.getHeropenen();
        restZaakActies.creeerenDocument = zaakActies.getCreeerenDocument();
        restZaakActies.toevoegenDocument = zaakActies.getToevoegenDocument();
        restZaakActies.ontkoppelenDocument = zaakActies.getOntkoppelenDocument();
        restZaakActies.koppelenZaak = zaakActies.getKoppelenZaak();
        restZaakActies.koppelenAanZaak = zaakActies.getKoppelenAanZaak();
        restZaakActies.versturenEmail = zaakActies.getVersturenEmail();
        restZaakActies.versturenOntvangstbevestiging = zaakActies.getVersturenOntvangstbevestiging();
        restZaakActies.toevoegenPersoon = zaakActies.getToevoegenPersoon();
        restZaakActies.toevoegenBedrijf = zaakActies.getToevoegenBedrijf();
        restZaakActies.verwijderenInitiator = zaakActies.getVerwijderenInitiator();
        restZaakActies.wijzigenToekenning = zaakActies.getWijzigenToekenning();
        restZaakActies.wijzigenData = zaakActies.getWijzigenData();
        restZaakActies.wijzigenOverig = zaakActies.getWijzigenOverig();
        restZaakActies.startenPlanItems = zaakActies.getStartenPlanItems();

        return restZaakActies;
    }
}