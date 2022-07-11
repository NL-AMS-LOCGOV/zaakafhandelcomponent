/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import javax.inject.Inject;

import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.RelevanteZaak;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.zaken.model.RESTGerelateerdeZaak;
import net.atos.zac.app.zaken.model.RelatieType;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.policy.output.ZaakActies;

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
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());
        Statustype statustype = null;
        if (zaak.getStatus() != null) {
            statustype = ztcClientService.readStatustype(zrcClientService.readStatus(zaak.getStatus()).getStatustype());
        }
        final ZaakActies zaakActies = policyService.readZaakActies(zaak, zaaktype, statustype);
        final RESTGerelateerdeZaak restGerelateerdeZaak = new RESTGerelateerdeZaak();
        restGerelateerdeZaak.identificatie = zaak.getIdentificatie();
        restGerelateerdeZaak.relatieType = relatieType;
        restGerelateerdeZaak.acties = actiesConverter.convert(zaakActies);
        if (zaakActies.getLezen()) {
            restGerelateerdeZaak.zaaktypeOmschrijving = zaaktype.getOmschrijving();
            restGerelateerdeZaak.startdatum = zaak.getStartdatum();
            if (statustype != null) {
                restGerelateerdeZaak.statustypeOmschrijving = statustype.getOmschrijving();
            }
        }
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
