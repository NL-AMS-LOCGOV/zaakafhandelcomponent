/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.audit.converter.zaken;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import net.atos.client.zgw.shared.model.ObjectType;
import net.atos.client.zgw.shared.model.audit.zaken.ZaakWijziging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.audit.converter.AbstractAuditWijzigingConverter;
import net.atos.zac.app.audit.model.RESTHistorieRegel;

public class AuditZaakWijzigingConverter extends AbstractAuditWijzigingConverter<ZaakWijziging> {

    @Inject
    private ZTCClientService ztcClientService;

    @Override
    public boolean supports(final ObjectType objectType) {
        return ObjectType.ZAAK == objectType;
    }

    @Override
    protected Stream<RESTHistorieRegel> doConvert(final ZaakWijziging zaakWijziging) {
        final Zaak oud = zaakWijziging.getOud();
        final Zaak nieuw = zaakWijziging.getNieuw();

        if (oud == null || nieuw == null) {
            return Stream.of(new RESTHistorieRegel("zaak", toWaarde(oud), toWaarde(nieuw)));
        }

        final List<RESTHistorieRegel> historieRegels = new LinkedList<>();
        checkWaarden("zaak.identificatie", oud.getIdentificatie(), nieuw.getIdentificatie(), historieRegels);
        checkWaarden("zaaktype", zaaktypeToWaarde(oud.getZaaktype()), zaaktypeToWaarde(nieuw.getZaaktype()), historieRegels);
        checkWaarden("kanaal", kanaalToWaarde(oud.getCommunicatiekanaal()), kanaalToWaarde(nieuw.getCommunicatiekanaal()), historieRegels);
        checkWaarden("vertrouwelijkheidaanduiding", enumToWaarde(oud.getVertrouwelijkheidaanduiding()), enumToWaarde(nieuw.getVertrouwelijkheidaanduiding()),
                     historieRegels);
        checkWaarden("registratiedatum", oud.getRegistratiedatum(), nieuw.getRegistratiedatum(), historieRegels);
        checkWaarden("startdatum", oud.getStartdatum(), nieuw.getStartdatum(), historieRegels);
        checkWaarden("einddatumGepland", oud.getEinddatumGepland(), nieuw.getEinddatumGepland(), historieRegels);
        checkWaarden("einddatum", oud.getEinddatum(), nieuw.getEinddatum(), historieRegels);
        checkWaarden("uiterlijkeEinddatumAfdoening", oud.getUiterlijkeEinddatumAfdoening(), nieuw.getUiterlijkeEinddatumAfdoening(), historieRegels);
        checkWaarden("omschrijving", oud.getOmschrijving(), nieuw.getOmschrijving(), historieRegels);
        checkWaarden("toelichting", oud.getToelichting(), nieuw.getToelichting(), historieRegels);

        return historieRegels.stream();
    }

    private String toWaarde(final Zaak zaak) {
        return zaak != null ? zaak.getIdentificatie() : null;
    }

    private String zaaktypeToWaarde(final URI zaaktype) {
        return zaaktype != null ? ztcClientService.readZaaktype(zaaktype).getIdentificatie() : null;
    }

    private String kanaalToWaarde(final URI kanaal) {
        // ToDo: Het kanaal moet worden opgehaald uit de VNG referentielijst
        return kanaal != null ? kanaal.toString() : null;
    }
}
