/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.brp.BRPClientService;
import net.atos.client.brp.model.Geboorte;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.NaamPersoon;
import net.atos.client.brp.model.Verblijfplaats;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.zaken.model.RESTPersoonOverzicht;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakKenmerk;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.util.ConfigurationService;
import net.atos.zac.util.PeriodUtil;

public class RESTZaakConverter {

    private static final String ONBEKEND = "<onbekend>";

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private BRPClientService brpClientService;

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

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

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

        restZaak.initiator = convertInitiator(zaak.getUrl());

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

    private RESTPersoonOverzicht convertInitiator(final URI zaak) {
        final Optional<RolNatuurlijkPersoon> initiatorForZaak = zgwApiService.findInitiatorForZaak(zaak);
        if (initiatorForZaak.isPresent()) {
            final String bsn = initiatorForZaak.get().getBetrokkeneIdentificatie().getInpBsn();
            return convertToIngeschrevenPersoon(bsn);
        } else {
            return null;
        }
    }

    private RESTPersoonOverzicht convertToIngeschrevenPersoon(final String bsn) {
        final RESTPersoonOverzicht persoonOverzicht = new RESTPersoonOverzicht();
        persoonOverzicht.bsn = bsn;
        final IngeschrevenPersoonHal ingeschrevenPersoon = brpClientService.findPersoon(bsn, null);
        if (ingeschrevenPersoon != null) {
            persoonOverzicht.naam = convertTotNaam(ingeschrevenPersoon.getNaam());
            persoonOverzicht.geboortedatum = convertToGeboortedatum(ingeschrevenPersoon.getGeboorte());
            persoonOverzicht.inschrijfadres = convertToInschrijfadres(ingeschrevenPersoon.getVerblijfplaats());
        } else {
            persoonOverzicht.naam = ONBEKEND;
            persoonOverzicht.geboortedatum = ONBEKEND;
            persoonOverzicht.inschrijfadres = ONBEKEND;
        }
        return persoonOverzicht;
    }

    private String convertTotNaam(final NaamPersoon naam) {
        if (naam != null) {
            return Stream.of(naam.getVoornamen(), naam.getVoorvoegsel(), naam.getGeslachtsnaam())
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));
        } else {
            return ONBEKEND;
        }
    }

    private String convertToGeboortedatum(final Geboorte geboorte) {
        if (geboorte != null && geboorte.getDatum() != null && geboorte.getDatum().getDatum() != null) {
            return geboorte.getDatum().getDatum().toString();
        } else {
            return ONBEKEND;
        }
    }

    private String convertToInschrijfadres(final Verblijfplaats verblijfplaats) {
        if (verblijfplaats != null) {
            return Stream.of(verblijfplaats.getStraat(),
                             Objects.toString(verblijfplaats.getHuisnummer(), null),
                             verblijfplaats.getHuisnummertoevoeging(),
                             verblijfplaats.getHuisletter(),
                             verblijfplaats.getWoonplaats())
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));
        } else {
            return ONBEKEND;
        }
    }
}
