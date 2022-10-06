/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.policy.converter;

import net.atos.zac.app.policy.model.RESTDocumentRechten;
import net.atos.zac.app.policy.model.RESTOverigeRechten;
import net.atos.zac.app.policy.model.RESTTaakRechten;
import net.atos.zac.app.policy.model.RESTWerklijstRechten;
import net.atos.zac.app.policy.model.RESTZaakRechten;
import net.atos.zac.policy.output.DocumentRechten;
import net.atos.zac.policy.output.OverigeRechten;
import net.atos.zac.policy.output.TaakRechten;
import net.atos.zac.policy.output.WerklijstRechten;
import net.atos.zac.policy.output.ZaakRechten;

public class RESTRechtenConverter {

    public RESTDocumentRechten convert(final DocumentRechten documentRechten) {
        final RESTDocumentRechten restDocumentRechten = new RESTDocumentRechten();
        restDocumentRechten.wijzigen = documentRechten.getWijzigen();
        restDocumentRechten.downloaden = documentRechten.getDownloaden();
        restDocumentRechten.ontgrendelen = documentRechten.getOntgrendelen();
        restDocumentRechten.vergrendelen = documentRechten.getVergrendelen();
        restDocumentRechten.verwijderen = documentRechten.getVerwijderen();
        restDocumentRechten.toevoegenNieuweVersie = documentRechten.getToevoegenNieuweVersie();
        restDocumentRechten.ondertekenen = documentRechten.getOndertekenen();
        return restDocumentRechten;
    }

    public RESTTaakRechten convert(final TaakRechten taakRechten) {
        final RESTTaakRechten restTaakRechten = new RESTTaakRechten();
        restTaakRechten.lezen = taakRechten.getLezen();
        restTaakRechten.toekennen = taakRechten.getToekennen();
        restTaakRechten.wijzigenFormulier = taakRechten.getWijzigenFormulier();
        restTaakRechten.wijzigen = taakRechten.getWijzigen();
        restTaakRechten.creeerenDocument = taakRechten.getCreeerenDocument();
        restTaakRechten.toevoegenDocument = taakRechten.getToevoegenDocument();
        return restTaakRechten;
    }

    public RESTZaakRechten convert(final ZaakRechten zaakRechten) {
        final RESTZaakRechten restZaakRechten = new RESTZaakRechten();
        restZaakRechten.lezen = zaakRechten.getLezen();
        restZaakRechten.opschorten = zaakRechten.getOpschorten();
        restZaakRechten.verlengen = zaakRechten.getVerlengen();
        restZaakRechten.hervatten = zaakRechten.getHervatten();
        restZaakRechten.afbreken = zaakRechten.getAfbreken();
        restZaakRechten.voortzetten = zaakRechten.getVoortzetten();
        restZaakRechten.heropenen = zaakRechten.getHeropenen();
        restZaakRechten.creeerenDocument = zaakRechten.getCreeerenDocument();
        restZaakRechten.toevoegenDocument = zaakRechten.getToevoegenDocument();
        restZaakRechten.koppelen = zaakRechten.getKoppelen();
        restZaakRechten.versturenEmail = zaakRechten.getVersturenEmail();
        restZaakRechten.versturenOntvangstbevestiging = zaakRechten.getVersturenOntvangstbevestiging();
        restZaakRechten.toevoegenBAGObject = zaakRechten.getToevoegenBAGObject();
        restZaakRechten.toevoegenInitiatorPersoon = zaakRechten.getToevoegenInitiatorPersoon();
        restZaakRechten.toevoegenInitiatorBedrijf = zaakRechten.getToevoegenInitiatorBedrijf();
        restZaakRechten.verwijderenInitiator = zaakRechten.getVerwijderenInitiator();
        restZaakRechten.toevoegenBetrokkenePersoon = zaakRechten.getToevoegenBetrokkenePersoon();
        restZaakRechten.toevoegenBetrokkeneBedrijf = zaakRechten.getToevoegenBetrokkeneBedrijf();
        restZaakRechten.verwijderenBetrokkene = zaakRechten.getVerwijderenBetrokkene();
        restZaakRechten.toekennen = zaakRechten.getToekennen();
        restZaakRechten.wijzigen = zaakRechten.getWijzigen();
        restZaakRechten.aanmakenTaak = zaakRechten.getAanmakenTaak();
        restZaakRechten.vastleggenBesluit = zaakRechten.getVastleggenBesluit();
        return restZaakRechten;
    }

    public RESTWerklijstRechten convert(final WerklijstRechten werklijstrechten) {
        final RESTWerklijstRechten restWerklijstRechten = new RESTWerklijstRechten();
        restWerklijstRechten.documentenInbox = werklijstrechten.getDocumentenInbox();
        restWerklijstRechten.documentenOntkoppeld = werklijstrechten.getDocumentenOntkoppeld();
        restWerklijstRechten.documentenOntkoppeldVerwijderen = werklijstrechten.getDocumentenOntkoppeldVerwijderen();
        restWerklijstRechten.zakenTaken = werklijstrechten.getZakenTaken();
        restWerklijstRechten.zakenTakenVerdelen = werklijstrechten.getZakenTakenVerdelen();
        return restWerklijstRechten;
    }

    public RESTOverigeRechten convert(final OverigeRechten overigeRechten) {
        final RESTOverigeRechten restOverigeRechten = new RESTOverigeRechten();
        restOverigeRechten.startenZaak = overigeRechten.getStartenZaak();
        restOverigeRechten.beheren = overigeRechten.getBeheren();
        restOverigeRechten.zoeken = overigeRechten.getZoeken();
        return restOverigeRechten;
    }
}
