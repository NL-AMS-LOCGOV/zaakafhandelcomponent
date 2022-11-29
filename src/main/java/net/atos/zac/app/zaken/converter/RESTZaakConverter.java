/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zaken.converter;

import static net.atos.client.zgw.ztc.model.Statustype.isHeropend;
import static net.atos.client.zgw.ztc.model.Statustype.isIntake;
import static net.atos.zac.app.klanten.model.klant.IdentificatieType.BSN;
import static net.atos.zac.app.klanten.model.klant.IdentificatieType.RSIN;
import static net.atos.zac.app.klanten.model.klant.IdentificatieType.VN;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.brc.BRCClientService;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.shared.model.Vertrouwelijkheidaanduiding;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Opschorting;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Verlenging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.app.identity.converter.RESTGroupConverter;
import net.atos.zac.app.identity.converter.RESTUserConverter;
import net.atos.zac.app.policy.converter.RESTRechtenConverter;
import net.atos.zac.app.zaken.model.RESTGerelateerdeZaak;
import net.atos.zac.app.zaken.model.RESTZaak;
import net.atos.zac.app.zaken.model.RESTZaakKenmerk;
import net.atos.zac.app.zaken.model.RESTZaakOpschortGegevens;
import net.atos.zac.app.zaken.model.RESTZaakVerlengGegevens;
import net.atos.zac.app.zaken.model.RelatieType;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.flowable.CaseVariablesService;
import net.atos.zac.policy.PolicyService;
import net.atos.zac.util.PeriodUtil;

public class RESTZaakConverter {

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private BRCClientService brcClientService;

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
    private RESTBesluitConverter besluitConverter;

    @Inject
    private RESTZaaktypeConverter zaaktypeConverter;

    @Inject
    private RESTRechtenConverter rechtenConverter;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private RESTCommunicatiekanaalConverter communicatiekanaalConverter;

    @Inject
    private RESTGeometryConverter restGeometryConverter;

    @Inject
    private PolicyService policyService;

    @Inject
    private CaseVariablesService caseVariablesService;

    public RESTZaak convert(final Zaak zaak) {
        final Status status = zaak.getStatus() != null ? zrcClientService.readStatus(zaak.getStatus()) : null;
        final Statustype statustype = status != null ? ztcClientService.readStatustype(status.getStatustype()) : null;
        return convert(zaak, status, statustype);
    }

    public RESTZaak convert(final Zaak zaak, final Status status, final Statustype statustype) {
        final RESTZaak restZaak = new RESTZaak();
        final Zaaktype zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype());

        brcClientService.findBesluit(zaak)
                .map(besluitConverter::convertToRESTBesluit)
                .ifPresent(besluit -> restZaak.besluit = besluit);
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
        if (status != null) {
            restZaak.status = zaakStatusConverter.convertToRESTZaakStatus(status, statustype);
        }

        restZaak.resultaat = zaakResultaatConverter.convert(zaak.getResultaat());

        restZaak.isOpgeschort = zaak.isOpgeschort();
        if (zaak.isOpgeschort() || StringUtils.isNotEmpty(zaak.getOpschorting().getReden())) {
            restZaak.redenOpschorting = zaak.getOpschorting().getReden();
        }

        restZaak.isVerlengd = zaak.isVerlengd();
        if (zaak.isVerlengd()) {
            restZaak.duurVerlenging = PeriodUtil.format(zaak.getVerlenging().getDuur());
            restZaak.redenVerlenging = zaak.getVerlenging().getReden();
        }

        restZaak.gerelateerdeZaken = convertGerelateerdeZaken(zaak);

        restZaak.zaakgeometrie = restGeometryConverter.convert(zaak.getZaakgeometrie());

        if (zaak.getKenmerken() != null) {
            restZaak.kenmerken = zaak.getKenmerken().stream()
                    .map(zaakKenmerk -> new RESTZaakKenmerk(zaakKenmerk.getKenmerk(), zaakKenmerk.getBron()))
                    .collect(Collectors.toList());
        }

        if (zaak.getCommunicatiekanaal() != null) {
            vrlClientService.findCommunicatiekanaal(uuidFromURI(zaak.getCommunicatiekanaal()))
                    .map(communicatiekanaalConverter::convert)
                    .ifPresent(communicatiekanaal -> restZaak.communicatiekanaal = communicatiekanaal);
        }

        restZaak.vertrouwelijkheidaanduiding = zaak.getVertrouwelijkheidaanduiding().toString();

        zgwApiService.findGroepForZaak(zaak)
                .map(groep -> groupConverter.convertGroupId(groep.getBetrokkeneIdentificatie().getIdentificatie()))
                .ifPresent(groep -> restZaak.groep = groep);

        zgwApiService.findBehandelaarForZaak(zaak)
                .map(behandelaar -> userConverter.convertUserId(
                        behandelaar.getBetrokkeneIdentificatie().getIdentificatie()))
                .ifPresent(behandelaar -> restZaak.behandelaar = behandelaar);

        zgwApiService.findInitiatorForZaak(zaak)
                .ifPresent(initiator -> {
                    restZaak.initiatorIdentificatieType = switch (initiator.getBetrokkeneType()) {
                        case NATUURLIJK_PERSOON -> BSN;
                        case VESTIGING -> VN;
                        case NIET_NATUURLIJK_PERSOON -> RSIN;
                        default -> null;
                    };
                    restZaak.initiatorIdentificatie = initiator.getIdentificatienummer();
                });

        restZaak.isHoofdzaak = zaak.is_Hoofdzaak();
        restZaak.isDeelzaak = zaak.isDeelzaak();
        restZaak.isOpen = zaak.isOpen();
        restZaak.isHeropend = isHeropend(statustype);
        restZaak.isInIntakeFase = isIntake(statustype);
        restZaak.isOntvangstbevestigingVerstuurd =
                caseVariablesService.findOntvangstbevestigingVerstuurd(zaak.getUuid()).orElse(false);
        restZaak.isBesluittypeAanwezig = isNotEmpty(zaaktype.getBesluittypen());
        restZaak.rechten = rechtenConverter.convert(policyService.readZaakRechten(zaak, zaaktype));

        restZaak.zaakdata = caseVariablesService.readZaakdata(zaak.getUuid());

        return restZaak;
    }

    public Zaak convert(final RESTZaak restZaak, final Zaaktype zaaktype) {
        final Zaak zaak = new Zaak(zaaktype.getUrl(), restZaak.startdatum,
                                   ConfiguratieService.BRON_ORGANISATIE,
                                   ConfiguratieService.VERANTWOORDELIJKE_ORGANISATIE);
        //aanvullen
        zaak.setOmschrijving(restZaak.omschrijving);
        zaak.setToelichting(restZaak.toelichting);
        zaak.setRegistratiedatum(LocalDate.now());

        if (restZaak.communicatiekanaal != null) {
            vrlClientService.findCommunicatiekanaal(
                            restZaak.communicatiekanaal.uuid)
                    .map(CommunicatieKanaal::getUrl)
                    .ifPresent(communicatieKanaal -> zaak.setCommunicatiekanaal(communicatieKanaal));
        }

        if (restZaak.vertrouwelijkheidaanduiding != null) {
            zaak.setVertrouwelijkheidaanduiding(
                    Vertrouwelijkheidaanduiding.fromValue(restZaak.vertrouwelijkheidaanduiding));
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
            vrlClientService.findCommunicatiekanaal(restZaak.communicatiekanaal.uuid)
                    .map(CommunicatieKanaal::getUrl)
                    .ifPresent(communicatieKanaal -> zaak.setCommunicatiekanaal(communicatieKanaal));
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
                                   ? new Verlenging(verlengGegevens.redenVerlenging,
                                                    verlenging.getDuur().plusDays(verlengGegevens.duurDagen))
                                   : new Verlenging(verlengGegevens.redenVerlenging,
                                                    Period.ofDays(verlengGegevens.duurDagen)));
        return zaak;
    }

    public Zaak convertToPatch(final RESTZaakOpschortGegevens restZaakOpschortGegevens) {
        final Zaak zaak = new Zaak();
        zaak.setEinddatumGepland(restZaakOpschortGegevens.einddatumGepland);
        zaak.setUiterlijkeEinddatumAfdoening(restZaakOpschortGegevens.uiterlijkeEinddatumAfdoening);
        if (restZaakOpschortGegevens.redenOpschorting != null) {
            zaak.setOpschorting(new Opschorting(restZaakOpschortGegevens.indicatieOpschorting,
                                                restZaakOpschortGegevens.redenOpschorting));
        }
        return zaak;
    }

    private List<RESTGerelateerdeZaak> convertGerelateerdeZaken(final Zaak zaak) {
        final List<RESTGerelateerdeZaak> gerelateerdeZaken = new ArrayList<>();
        if (zaak.getHoofdzaak() != null) {
            gerelateerdeZaken.add(gerelateerdeZaakConverter.convert(zrcClientService.readZaak(zaak.getHoofdzaak()),
                                                                    RelatieType.HOOFDZAAK));
        }
        zaak.getDeelzaken().stream()
                .map(zrcClientService::readZaak)
                .map(deelzaak -> gerelateerdeZaakConverter.convert(deelzaak, RelatieType.DEELZAAK))
                .forEach(gerelateerdeZaken::add);
        zaak.getRelevanteAndereZaken().stream()
                .map(gerelateerdeZaakConverter::convert)
                .forEach(gerelateerdeZaken::add);
        return gerelateerdeZaken;
    }
}
