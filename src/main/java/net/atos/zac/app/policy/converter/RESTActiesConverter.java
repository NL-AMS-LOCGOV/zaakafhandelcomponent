/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy.converter;

import net.atos.zac.app.policy.model.RESTDocumentActies;
import net.atos.zac.app.policy.model.RESTOverigActies;
import net.atos.zac.app.policy.model.RESTTaakActies;
import net.atos.zac.app.policy.model.RESTWerklijstActies;
import net.atos.zac.app.policy.model.RESTZaakActies;
import net.atos.zac.policy.output.DocumentActies;
import net.atos.zac.policy.output.OverigActies;
import net.atos.zac.policy.output.TaakActies;
import net.atos.zac.policy.output.WerklijstActies;
import net.atos.zac.policy.output.ZaakActies;

public class RESTActiesConverter {

    public RESTDocumentActies convert(final DocumentActies acties) {
        final RESTDocumentActies restActies = new RESTDocumentActies();
        restActies.wijzigen = acties.getWijzigen();
        restActies.downloaden = acties.getDownloaden();
        restActies.ontgrendelen = acties.getOntgrendelen();
        restActies.vergrendelen = acties.getVergrendelen();
        restActies.verwijderen = acties.getVerwijderen();
        restActies.toevoegenNieuweVersie = acties.getToevoegenNieuweVersie();
        restActies.ondertekenen = acties.getOndertekenen();
        return restActies;
    }

    public RESTTaakActies convert(final TaakActies taakActies) {
        final RESTTaakActies restTaakActies = new RESTTaakActies();
        restTaakActies.toekennen = taakActies.getToekennen();
        restTaakActies.wijzigenFormulier = taakActies.getWijzigenFormulier();
        restTaakActies.wijzigen = taakActies.getWijzigen();
        restTaakActies.creeerenDocument = taakActies.getCreeerenDocument();
        restTaakActies.toevoegenDocument = taakActies.getToevoegenDocument();
        return restTaakActies;
    }

    public RESTZaakActies convert(final ZaakActies zaakActies) {
        final RESTZaakActies restZaakActies = new RESTZaakActies();
        restZaakActies.lezen = zaakActies.getLezen();
        restZaakActies.opschorten = zaakActies.getOpschorten();
        restZaakActies.verlengen = zaakActies.getVerlengen();
        restZaakActies.hervatten = zaakActies.getHervatten();
        restZaakActies.afbreken = zaakActies.getAfbreken();
        restZaakActies.voortzetten = zaakActies.getVoortzetten();
        restZaakActies.heropenen = zaakActies.getHeropenen();
        restZaakActies.creeerenDocument = zaakActies.getCreeerenDocument();
        restZaakActies.toevoegenDocument = zaakActies.getToevoegenDocument();
        restZaakActies.koppelen = zaakActies.getKoppelen();
        restZaakActies.versturenEmail = zaakActies.getVersturenEmail();
        restZaakActies.versturenOntvangstbevestiging = zaakActies.getVersturenOntvangstbevestiging();
        restZaakActies.toevoegenBAGObject = zaakActies.getToevoegenBAGObject();
        restZaakActies.toevoegenInitiatorPersoon = zaakActies.getToevoegenInitiatorPersoon();
        restZaakActies.toevoegenInitiatorBedrijf = zaakActies.getToevoegenInitiatorBedrijf();
        restZaakActies.verwijderenInitiator = zaakActies.getVerwijderenInitiator();
        restZaakActies.toevoegenBetrokkenePersoon = zaakActies.getToevoegenBetrokkenePersoon();
        restZaakActies.toevoegenBetrokkeneBedrijf = zaakActies.getToevoegenBetrokkeneBedrijf();
        restZaakActies.verwijderenBetrokkene = zaakActies.getVerwijderenBetrokkene();
        restZaakActies.toekennen = zaakActies.getToekennen();
        restZaakActies.wijzigen = zaakActies.getWijzigen();
        restZaakActies.aanmakenTaak = zaakActies.getAanmakenTaak();
        restZaakActies.vastleggenBesluit = zaakActies.getVastleggenBesluit();
        return restZaakActies;
    }

    public RESTWerklijstActies convert(final WerklijstActies werklijstActies) {
        final RESTWerklijstActies restWerklijstActies = new RESTWerklijstActies();
        restWerklijstActies.documentenInbox = werklijstActies.getDocumentenInbox();
        restWerklijstActies.documentenOntkoppeld = werklijstActies.getDocumentenOntkoppeld();
        restWerklijstActies.documentenOntkoppeldVerwijderen = werklijstActies.getDocumentenOntkoppeldVerwijderen();
        restWerklijstActies.zakenTaken = werklijstActies.getZakenTaken();
        restWerklijstActies.zakenTakenVerdelen = werklijstActies.getZakenTakenVerdelen();
        return restWerklijstActies;
    }

    public RESTOverigActies convert(final OverigActies overigActies) {
        final RESTOverigActies restOverigActies = new RESTOverigActies();
        restOverigActies.startenZaak = overigActies.getStartenZaak();
        restOverigActies.beheren = overigActies.getBeheren();
        restOverigActies.zoeken = overigActies.getZoeken();
        return restOverigActies;
    }
}
