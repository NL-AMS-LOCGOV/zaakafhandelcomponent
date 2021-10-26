/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.rechten.ZaakRechten;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakKenmerk;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.handle.HandleService;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.PeriodUtil;

public class RESTZaakConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private RESTZaakStatusConverter zaakStatusConverter;

    @Inject
    private RESTZaakResultaatConverter zaakResultaatConverter;

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    @EJB
    private HandleService handleService;

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
        restZaak.eigenschappen = RESTZaakEigenschappenConverter.convert(zaak.getEigenschappen());
        restZaak.gerelateerdeZaken = RESTGerelateerdeZaakConverter.getGerelateerdeZaken(zaak);
        if (zaak.getZaakgeometrie() != null) {
            restZaak.zaakgeometrie = zaak.getZaakgeometrie().getType();
        }
        if (zaak.getKenmerken() != null) {
            restZaak.kenmerken = zaak.getKenmerken().stream().map(zaakKenmerk -> new RESTZaakKenmerk(zaakKenmerk.getKenmerk(), zaakKenmerk.getBron()))
                    .collect(Collectors.toList());
        }
        //restZaakView.communicatiekanaal
        restZaak.vertrouwelijkheidaanduiding = zaak.getVertrouwelijkheidaanduiding().toString();

        final String groepId = handleService.ophalenZaakBehandelaarGroepId(zaak.getUrl(), zaak.getZaaktype());
        restZaak.groep = groepConverter.convertGroupId(groepId);

        final String behandelaarId = handleService.ophalenZaakBehandelaarMedewerkerId(zaak.getUrl(), zaak.getZaaktype());
        restZaak.behandelaar = medewerkerConverter.convertUserId(behandelaarId);

        //TODO ESUITEDEV-25820 rechtencheck met solrZaak
        restZaak.rechten = getRechten(behandelaarId, groepId);
        return restZaak;
    }

    public Zaak convert(final RESTZaak restZaak) {

        final Zaak zaak = new Zaak(ztcClientService.getZaaktype(restZaak.zaaktype.identificatie).getUrl(), restZaak.startdatum,
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

    private RESTZaaktype getZaaktype(final URI zaaktypeURI) {
        final Zaaktype zaaktype = ztcClientService.getZaaktype(zaaktypeURI);
        return RESTZaaktypeConverter.convert(zaaktype);
    }

    private URI getCommunicatieKanaal(final String id) {
        //TODO het daadwerkelijke kanaal moet worden opgezocht
        return id != null ? URI.create(id) : null;
    }

    private Map<String, Boolean> getRechten(final String behandelaarId, final String groepId) {
        final Map<String, Boolean> rechten = new HashMap<>();

        //TODO ESUITEDEV-25820 rechtencheck met solrZaak
        rechten.put("toekennenToegestaan", ZaakRechten.isToekennenToegestaan(ingelogdeMedewerker, behandelaarId, groepId));
        rechten.put("vrijgevenToegestaan", ZaakRechten.isVrijgevenToegestaan(ingelogdeMedewerker, behandelaarId, groepId));
        rechten.put("kenToeAanMijToegestaan", ZaakRechten.isKenToeAanMijToegestaan(ingelogdeMedewerker, behandelaarId, groepId));
        rechten.put("behandelenToegestaan", ZaakRechten.isBehandelenToegestaan(ingelogdeMedewerker, behandelaarId, groepId));

        return rechten;
    }
}


