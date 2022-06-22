/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import static net.atos.client.zgw.shared.ZGWApiService.STATUSTYPE_HEROPEND_OMSCHRIJVING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.net.URI;
import java.time.Period;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Opschorting;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Verlenging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakKenmerk;
import net.atos.zac.app.zaken.model.RESTZaakOpschortGegevens;
import net.atos.zac.app.zaken.model.RESTZaakVerlengGegevens;
import net.atos.zac.app.zaken.model.RESTZaaktype;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.util.PeriodUtil;
import net.atos.zac.util.UriUtil;

import org.apache.commons.collections4.CollectionUtils;

public class RESTZaakConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private RESTZaakStatusConverter zaakStatusConverter;

    @Inject
    private RESTZaakResultaatConverter zaakResultaatConverter;

    @Inject
    private RESTGroupConverter groupConverter;

    @Inject
    private RESTGerelateerdeZaakConverter gerelateerdeZaakConverter;

    @Inject
    private RESTUserConverter userConverter;

    @Inject
    private RESTZaakEigenschappenConverter zaakEigenschappenConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    @Inject
    private RESTZaakRechtenConverter zaakRechtenConverter;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private RESTCommunicatiekanaalConverter communicatiekanaalConverter;

    @Inject
    private RESTGeometryConverter restGeometryConverter;

    @Inject
    private FlowableService flowableService;

    public RESTZaak convert(final Zaak zaak) {
        final RESTZaak restZaak = new RESTZaak();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());

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
        if (zaak.getArchiefnominatie() != null) {
            restZaak.archiefNominatie = zaak.getArchiefnominatie().name();
            restZaak.archiefActiedatum = zaak.getArchiefactiedatum();
        }
        restZaak.omschrijving = zaak.getOmschrijving();
        restZaak.toelichting = zaak.getToelichting();
        restZaak.zaaktype = zaaktypeConverter.convert(zaaktype);
        restZaak.status = zaakStatusConverter.convertToRESTZaakStatus(zaak.getStatus());
        restZaak.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());

        if (zaak.getOpschorting() != null) {
            restZaak.indicatieOpschorting = zaak.getOpschorting().getIndicatie();
            restZaak.redenOpschorting = zaak.getOpschorting().getReden();
        }
        if (zaak.getVerlenging() != null) {
            restZaak.indicatieVerlenging = zaak.getVerlenging().getDuur() != null;
            restZaak.duurVerlenging = PeriodUtil.format(zaak.getVerlenging().getDuur());
            restZaak.redenVerlenging = zaak.getVerlenging().getReden();
        }

        restZaak.eigenschappen = zaakEigenschappenConverter.convert(zaak.getEigenschappen());
        restZaak.gerelateerdeZaken = gerelateerdeZaakConverter.getGerelateerdeZaken(zaak);
        restZaak.zaakgeometrie = restGeometryConverter.convert(zaak.getZaakgeometrie());

        if (zaak.getKenmerken() != null) {
            restZaak.kenmerken = zaak.getKenmerken().stream().map(zaakKenmerk -> new RESTZaakKenmerk(zaakKenmerk.getKenmerk(), zaakKenmerk.getBron()))
                    .collect(Collectors.toList());
        }

        if (zaak.getCommunicatiekanaal() != null) {
            final UUID communicatiekanaalUUID = UriUtil.uuidFromURI(zaak.getCommunicatiekanaal());
            final CommunicatieKanaal communicatieKanaal = vrlClientService.findCommunicatiekanaal(communicatiekanaalUUID);
            restZaak.communicatiekanaal = communicatiekanaalConverter.convert(communicatieKanaal, communicatiekanaalUUID);
        }

        restZaak.vertrouwelijkheidaanduiding = zaak.getVertrouwelijkheidaanduiding().toString();

        final RolOrganisatorischeEenheid groep = zgwApiService.findGroepForZaak(zaak.getUrl());
        if (groep != null) {
            restZaak.groep = groupConverter.convertGroupId(groep.getBetrokkeneIdentificatie().getIdentificatie());
        }

        final RolMedewerker behandelaar = zgwApiService.findBehandelaarForZaak(zaak.getUrl());
        if (behandelaar != null) {
            restZaak.behandelaar = userConverter.convertUserId(behandelaar.getBetrokkeneIdentificatie().getIdentificatie());
        }

        final Rol<?> initiator = zgwApiService.findRolForZaak(zaak, AardVanRol.INITIATOR);
        if (initiator != null) {
            restZaak.initiatorIdentificatie = initiator.getIdentificatienummer();
        }

        restZaak.ontvangstbevestigingVerstuurd = isTrue(flowableService.findOntvangstbevestigingVerstuurd(zaak.getUuid()));
        restZaak.isHoofdzaak = CollectionUtils.isNotEmpty(zaak.getDeelzaken());
        restZaak.isDeelzaak = zaak.getHoofdzaak() != null;

        restZaak.heropend = restZaak.status != null && restZaak.status.naam.equals(STATUSTYPE_HEROPEND_OMSCHRIJVING);
        restZaak.rechten = zaakRechtenConverter.convertToRESTZaakRechten(zaak, restZaak.heropend);

        return restZaak;
    }

    public Zaak convert(final RESTZaak restZaak) {

        final Zaak zaak = new Zaak(ztcClientService.readZaaktypeUrl(restZaak.zaaktype.identificatie), restZaak.startdatum,
                                   ConfiguratieService.BRON_ORGANISATIE, ConfiguratieService.VERANTWOORDELIJKE_ORGANISATIE);
        //aanvullen
        zaak.setOmschrijving(restZaak.omschrijving);
        zaak.setToelichting(restZaak.toelichting);

        zaak.setRegistratiedatum(restZaak.registratiedatum);

        if (restZaak.communicatiekanaal != null) {
            final CommunicatieKanaal communicatieKanaal = vrlClientService.findCommunicatiekanaal(restZaak.communicatiekanaal.uuid);
            zaak.setCommunicatiekanaal(communicatieKanaal.getUrl());
        }

        if (restZaak.vertrouwelijkheidaanduiding != null) {
            zaak.setVertrouwelijkheidaanduiding(Vertrouwelijkheidaanduiding.fromValue(restZaak.vertrouwelijkheidaanduiding));
        }

        zaak.setZaakgeometrie(restGeometryConverter.convert(restZaak.zaakgeometrie));

        return zaak;
    }

    public Zaak convertToPatch(final RESTZaak restZaak) {
        final Zaak zaak = new Zaak();
        zaak.setToelichting(restZaak.toelichting);
        zaak.setOmschrijving(restZaak.omschrijving);
        zaak.setStartdatum(restZaak.startdatum);
        zaak.setEinddatumGepland(restZaak.einddatumGepland);
        zaak.setUiterlijkeEinddatumAfdoening(restZaak.uiterlijkeEinddatumAfdoening);
        if (restZaak.vertrouwelijkheidaanduiding != null) {
            zaak.setVertrouwelijkheidaanduiding(
                    Vertrouwelijkheidaanduiding.fromValue(restZaak.vertrouwelijkheidaanduiding));
        }
        if (restZaak.communicatiekanaal != null) {
            final CommunicatieKanaal communicatieKanaal = vrlClientService.findCommunicatiekanaal(
                    restZaak.communicatiekanaal.uuid);
            zaak.setCommunicatiekanaal(communicatieKanaal.getUrl());
        }
        zaak.setZaakgeometrie(restGeometryConverter.convert(restZaak.zaakgeometrie));
        return zaak;
    }

    public Zaak convertToPatch(final UUID zaakUUID, final RESTZaakVerlengGegevens verlengGegevens) {
        final Zaak zaak = new Zaak();
        zaak.setEinddatumGepland(verlengGegevens.einddatumGepland);
        zaak.setUiterlijkeEinddatumAfdoening(verlengGegevens.uiterlijkeEinddatumAfdoening);
        final Verlenging verlenging = zrcClientService.readZaak(zaakUUID).getVerlenging();
        zaak.setVerlenging(verlenging != null && verlenging.getDuur() != null
                                   ? new Verlenging(verlengGegevens.redenVerlenging, verlenging.getDuur().plusDays(verlengGegevens.duurDagen))
                                   : new Verlenging(verlengGegevens.redenVerlenging, Period.ofDays(verlengGegevens.duurDagen)));
        return zaak;
    }

    public Zaak convertToPatch(final RESTZaakOpschortGegevens restZaakOpschortGegevens) {
        final Zaak zaak = new Zaak();
        zaak.setEinddatumGepland(restZaakOpschortGegevens.einddatumGepland);
        zaak.setUiterlijkeEinddatumAfdoening(restZaakOpschortGegevens.uiterlijkeEinddatumAfdoening);
        if (restZaakOpschortGegevens.redenOpschorting != null) {
            zaak.setOpschorting(new Opschorting(restZaakOpschortGegevens.indicatieOpschorting, restZaakOpschortGegevens.redenOpschorting));
        }
        return zaak;
    }

    private RESTZaaktype getZaaktype(final URI zaaktypeURI) {
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaaktypeURI);
        return zaaktypeConverter.convert(zaaktype);
    }
}
