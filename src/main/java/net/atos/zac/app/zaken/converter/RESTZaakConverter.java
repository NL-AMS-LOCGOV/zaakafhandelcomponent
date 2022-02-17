/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakKenmerk;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.PeriodUtil;

public class RESTZaakConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakStatusConverter zaakStatusConverter;

    @Inject
    private RESTZaakResultaatConverter zaakResultaatConverter;

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTGerelateerdeZaakConverter gerelateerdeZaakConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    private RESTZaakEigenschappenConverter zaakEigenschappenConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    public RESTZaak convert(final Zaak zaak) {
        final RESTZaak restZaak = new RESTZaak();

        restZaak.identificatie = zaak.getIdentificatie();
        restZaak.uuid = zaak.getUuid();
        restZaak.bronorganisatie = zaak.getBronorganisatie();
        restZaak.verantwoordelijkeOrganisatie = zaak.getVerantwoordelijkeOrganisatie();
        restZaak.startdatum = zaak.getStartdatum();
        restZaak.einddatum = zaak.getEinddatum();
        restZaak.einddatumGepland = zaak.getEinddatumGepland();
        restZaak.uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening();
        restZaak.publicatiedatum = zaak.getPublicatiedatum();
        restZaak.registratiedatum = zaak.getRegistratiedatum();
        restZaak.omschrijving = zaak.getOmschrijving();
        restZaak.toelichting = zaak.getToelichting();
        restZaak.zaaktype = getZaaktype(zaak.getZaaktype());
        restZaak.status = zaakStatusConverter.convert(zaak.getStatus());
        restZaak.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());
        if (zaak.getOpschorting() != null) {
            restZaak.indicatieOpschorting = zaak.getOpschorting().getIndicatie();
            restZaak.redenOpschorting = zaak.getOpschorting().getReden();
        }

        if (zaak.getVerlenging() != null) {
            restZaak.redenVerlenging = zaak.getVerlenging().getReden();
            restZaak.duurVerlenging = PeriodUtil.format(zaak.getVerlenging().getDuur());
            restZaak.indicatieVerlenging = restZaak.duurVerlenging != null;
        }
        restZaak.eigenschappen = zaakEigenschappenConverter.convert(zaak.getEigenschappen());
        restZaak.gerelateerdeZaken = gerelateerdeZaakConverter.getGerelateerdeZaken(zaak);
        if (zaak.getZaakgeometrie() != null) {
            restZaak.zaakgeometrie = zaak.getZaakgeometrie().getType();
        }
        if (zaak.getKenmerken() != null) {
            restZaak.kenmerken = zaak.getKenmerken().stream().map(zaakKenmerk -> new RESTZaakKenmerk(zaakKenmerk.getKenmerk(), zaakKenmerk.getBron()))
                    .collect(Collectors.toList());
        }
        //restZaakView.communicatiekanaal
        restZaak.vertrouwelijkheidaanduiding = zaak.getVertrouwelijkheidaanduiding().toString();

        final String groepId = zgwApiService.findGroepForZaak(zaak.getUrl())
                .filter(Objects::nonNull)
                .map(groep -> groep.getBetrokkeneIdentificatie().getIdentificatie())
                .orElse(null);
        restZaak.groep = groepConverter.convertGroupId(groepId);

        final String behandelaarId = zgwApiService.findBehandelaarForZaak(zaak.getUrl())
                .filter(Objects::nonNull)
                .map(behandelaar -> behandelaar.getBetrokkeneIdentificatie().getIdentificatie())
                .orElse(null);
        restZaak.behandelaar = medewerkerConverter.convertUserId(behandelaarId);

        zgwApiService.findInitiatorForZaak(zaak.getUrl()).ifPresent(
                rolNatuurlijkPersoon -> restZaak.initiatorBSN = rolNatuurlijkPersoon.getBetrokkeneIdentificatie().getInpBsn()
        );

        return restZaak;
    }

    public Zaak convert(final RESTZaak restZaak) {

        final Zaak zaak = new Zaak(ztcClientService.readZaaktypeUrl(restZaak.zaaktype.identificatie), restZaak.startdatum,
                                   ConfigurationService.BRON_ORGANISATIE, ConfigurationService.VERANTWOORDELIJKE_ORGANISATIE);
        //aanvullen
        zaak.setOmschrijving(restZaak.omschrijving);
        zaak.setToelichting(restZaak.toelichting);

        zaak.setEinddatumGepland(restZaak.startdatum);
        zaak.setRegistratiedatum(restZaak.registratiedatum);

        zaak.setCommunicatiekanaal(getCommunicatieKanaal(restZaak.communicatiekanaal));
        if (restZaak.vertrouwelijkheidaanduiding != null) {
            zaak.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.valueOf(restZaak.vertrouwelijkheidaanduiding));
        }

        return zaak;
    }

    public Zaak convertToPatch(final RESTZaak restZaak) {
        final Zaak zaak = new Zaak();
        zaak.setToelichting(restZaak.toelichting);
        zaak.setOmschrijving(restZaak.omschrijving);
        zaak.setStartdatum(restZaak.startdatum);
        zaak.setEinddatumGepland(restZaak.einddatumGepland);
        zaak.setUiterlijkeEinddatumAfdoening(restZaak.uiterlijkeEinddatumAfdoening);
        zaak.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.valueOf(restZaak.vertrouwelijkheidaanduiding));
        return zaak;
    }

    private RESTZaaktype getZaaktype(final URI zaaktypeURI) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaaktypeURI);
        return zaaktypeConverter.convert(zaaktype);
    }

    private URI getCommunicatieKanaal(final String id) {
        //TODO het daadwerkelijke kanaal moet worden opgezocht
        return id != null ? URI.create(id) : null;
    }
}
