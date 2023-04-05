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
        restDocumentRechten.lezen = documentRechten.getLezen();
        restDocumentRechten.wijzigen = documentRechten.getWijzigen();
        restDocumentRechten.ontgrendelen = documentRechten.getOntgrendelen();
        restDocumentRechten.vergrendelen = documentRechten.getVergrendelen();
        restDocumentRechten.verwijderen = documentRechten.getVerwijderen();
        restDocumentRechten.ondertekenen = documentRechten.getOndertekenen();
        return restDocumentRechten;
    }

    public RESTTaakRechten convert(final TaakRechten taakRechten) {
        final RESTTaakRechten restTaakRechten = new RESTTaakRechten();
        restTaakRechten.lezen = taakRechten.getLezen();
        restTaakRechten.wijzigen = taakRechten.getWijzigen();
        restTaakRechten.toekennen = taakRechten.getToekennen();
        return restTaakRechten;
    }

    public RESTZaakRechten convert(final ZaakRechten zaakRechten) {
        final RESTZaakRechten restZaakRechten = new RESTZaakRechten();
        restZaakRechten.lezen = zaakRechten.getLezen();
        restZaakRechten.wijzigen = zaakRechten.getWijzigen();
        restZaakRechten.toekennen = zaakRechten.getToekennen();
        restZaakRechten.behandelen = zaakRechten.getBehandelen();
        restZaakRechten.afbreken = zaakRechten.getAfbreken();
        restZaakRechten.heropenen = zaakRechten.getHeropenen();
        restZaakRechten.wijzigenDoorlooptijd = zaakRechten.getWijzigenDoorlooptijd();

        return restZaakRechten;
    }

    public RESTWerklijstRechten convert(final WerklijstRechten werklijstrechten) {
        final RESTWerklijstRechten restWerklijstRechten = new RESTWerklijstRechten();
        restWerklijstRechten.documentenInbox = werklijstrechten.getDocumentenInbox();
        restWerklijstRechten.documentenOntkoppeld = werklijstrechten.getDocumentenOntkoppeld();
        restWerklijstRechten.documentenOntkoppeldVerwijderen = werklijstrechten.getDocumentenOntkoppeldVerwijderen();
        restWerklijstRechten.productaanvragenInbox = werklijstrechten.getProductaanvragenInbox();
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
