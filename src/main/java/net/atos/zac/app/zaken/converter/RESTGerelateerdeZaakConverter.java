/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RelevanteZaak;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.zaken.model.RESTGerelateerdeZaak;
import net.atos.zac.app.zaken.model.RelatieType;
import net.atos.zac.policy.PolicyService;

public class RESTGerelateerdeZaakConverter {

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private RESTZaakActiesConverter actiesConverter;

    @Inject
    private PolicyService policyService;

    public RESTGerelateerdeZaak convert(final Zaak zaak, final RelatieType relatieType) {
        final RESTGerelateerdeZaak restGerelateerdeZaak = new RESTGerelateerdeZaak();
        restGerelateerdeZaak.relatieType = relatieType;
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        restGerelateerdeZaak.zaaktypeOmschrijving = zaaktype.getOmschrijving();
        Statustype statustype = null;
        if (zaak.getStatus() != null) {
            final Status status = zrcClientService.readStatus(zaak.getStatus());
            statustype = ztcClientService.readStatustype(status.getStatustype());
            restGerelateerdeZaak.statustypeOmschrijving = statustype.getOmschrijving();
        }
        restGerelateerdeZaak.identificatie = zaak.getIdentificatie();
        restGerelateerdeZaak.startdatum = zaak.getStartdatum();
        restGerelateerdeZaak.acties = actiesConverter.convert(policyService.readZaakActies(zaak, zaaktype, statustype));
        return restGerelateerdeZaak;
    }

    public RESTGerelateerdeZaak convert(final RelevanteZaak relevanteZaak) {
        final Zaak zaak = zrcClientService.readZaak(relevanteZaak.getUrl());
        return switch (relevanteZaak.getAardRelatie()) {
            case VERVOLG -> convert(zaak, RelatieType.VERVOLG);
            case ONDERWERP -> convert(zaak, RelatieType.RELEVANT);
            case BIJDRAGE -> convert(zaak, RelatieType.BIJDRAGE);
        };
    }
}
