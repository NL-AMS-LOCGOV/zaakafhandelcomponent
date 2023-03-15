/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.brc.model.Vervalreden;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjectConverter;
import net.atos.zac.app.zaken.model.RESTBesluit;
import net.atos.zac.app.zaken.model.RESTBesluitVastleggenGegevens;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.util.UriUtil;

public class RESTBesluitConverter {

    @Inject
    private RESTBesluittypeConverter restBesluittypeConverter;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private BRCClientService brcClientService;

    @Inject
    private RESTInformatieobjectConverter informatieobjectConverter;

    @Inject
    private DRCClientService drcClientService;

    public RESTBesluit convertToRESTBesluit(final Besluit besluit) {
        final RESTBesluit restBesluit = new RESTBesluit();
        restBesluit.uuid = UriUtil.uuidFromURI(besluit.getUrl());
        restBesluit.besluittype = restBesluittypeConverter.convertToRESTBesluittype(besluit.getBesluittype());
        restBesluit.datum = besluit.getDatum();
        restBesluit.identificatie = besluit.getIdentificatie();
        restBesluit.url = besluit.getUrl();
        restBesluit.toelichting = besluit.getToelichting();
        restBesluit.ingangsdatum = besluit.getIngangsdatum();
        restBesluit.vervaldatum = besluit.getVervaldatum();
        if (restBesluit.vervaldatum != null) {
            restBesluit.vervalreden = besluit.getVervalreden();
        }
        restBesluit.informatieobjecten = informatieobjectConverter.convertInformatieobjectenToREST(
                listBesluitInformatieobjecten(besluit));
        return restBesluit;
    }

    public List<RESTBesluit> convertToRESTBesluit(final List<Besluit> besluiten) {
        return besluiten.stream()
                .map(this::convertToRESTBesluit)
                .toList();
    }

    public Besluit convertToBesluit(final Zaak zaak, final RESTBesluitVastleggenGegevens besluitToevoegenGegevens) {
        final Besluit besluit = new Besluit();
        besluit.setZaak(zaak.getUrl());
        besluit.setBesluittype(ztcClientService.readBesluittype(besluitToevoegenGegevens.besluittypeUuid).getUrl());
        besluit.setDatum(LocalDate.now());
        besluit.setIngangsdatum(besluitToevoegenGegevens.ingangsdatum);
        besluit.setVervaldatum(besluitToevoegenGegevens.vervaldatum);
        if (besluitToevoegenGegevens.vervaldatum != null) {
            besluit.setVervalreden(Vervalreden.TIJDELIJK);
        }
        besluit.setVerantwoordelijkeOrganisatie(ConfiguratieService.VERANTWOORDELIJKE_ORGANISATIE);
        besluit.setToelichting(besluitToevoegenGegevens.toelichting);
        return besluit;
    }

    public List<EnkelvoudigInformatieobject> listBesluitInformatieobjecten(final Besluit besluit) {
        return brcClientService.listBesluitInformatieobjecten(besluit.getUrl()).stream()
                .map(besluitInformatieobject -> drcClientService.readEnkelvoudigInformatieobject(
                        besluitInformatieobject.getInformatieobject()))
                .collect(Collectors.toList());
    }

}
