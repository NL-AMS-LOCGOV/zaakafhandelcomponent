/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.documentcreatie.converter;

import static net.atos.client.or.shared.util.URIUtil.getUUID;
import static net.atos.client.zgw.zrc.model.Objecttype.OVERIGE;
import static net.atos.zac.aanvraag.ProductaanvraagService.OBJECT_TYPE_OVERIGE_PRODUCTAANVRAAG;
import static net.atos.zac.util.UriUtil.uuidFromURI;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.TaskInfo;

import net.atos.client.brp.BRPClientService;
import net.atos.client.brp.model.IngeschrevenPersoonHal;
import net.atos.client.brp.model.NaamPersoon;
import net.atos.client.brp.model.Verblijfplaats;
import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.zac.aanvraag.ProductaanvraagService;
import net.atos.zac.authentication.LoggedInUser;
import net.atos.zac.documentcreatie.model.AanvragerData;
import net.atos.zac.documentcreatie.model.Data;
import net.atos.zac.documentcreatie.model.DocumentCreatieGegevens;
import net.atos.zac.documentcreatie.model.GebruikerData;
import net.atos.zac.documentcreatie.model.StartformulierData;
import net.atos.zac.documentcreatie.model.TaakData;
import net.atos.zac.documentcreatie.model.ZaakData;
import net.atos.zac.flowable.TaakVariabelenService;
import net.atos.zac.flowable.TakenService;
import net.atos.zac.identity.IdentityService;

public class DataConverter {

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static final String FIELDS_PERSOON =
            "burgerservicenummer," +
                    "naam.voornamen," +
                    "naam.voorvoegsel," +
                    "naam.geslachtsnaam," +
                    "verblijfplaats.straat," +
                    "verblijfplaats.huisnummer," +
                    "verblijfplaats.huisnummertoevoeging," +
                    "verblijfplaats.huisletter," +
                    "verblijfplaats.postcode," +
                    "verblijfplaats.woonplaats";

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private BRPClientService brpClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private ObjectsClientService objectsClientService;

    @Inject
    private TakenService takenService;

    @Inject
    private TaakVariabelenService taakVariabelenService;

    @Inject
    private IdentityService identityService;

    @Inject
    private ProductaanvraagService productaanvraagService;

    public Data createData(final DocumentCreatieGegevens documentCreatieGegevens, final LoggedInUser loggedInUser) {
        final Data data = new Data();
        data.gebruiker = createGebruikerData(loggedInUser);
        data.zaak = createZaakData(documentCreatieGegevens.getZaak());
        data.aanvrager = createAanvragerData(documentCreatieGegevens.getZaak());
        data.startformulier = createStartformulierData(documentCreatieGegevens.getZaak().getUrl());
        if (documentCreatieGegevens.getTaskId() != null) {
            data.taak = createTaakData(documentCreatieGegevens.getTaskId());
        }
        return data;
    }

    private GebruikerData createGebruikerData(final LoggedInUser loggedInUser) {
        final GebruikerData gebruikerData = new GebruikerData();
        gebruikerData.id = loggedInUser.getId();
        gebruikerData.naam = loggedInUser.getFullName();
        return gebruikerData;
    }

    private ZaakData createZaakData(final Zaak zaak) {
        final ZaakData zaakData = new ZaakData();

        zaakData.identificatie = zaak.getIdentificatie();
        zaakData.omschrijving = zaak.getOmschrijving();
        zaakData.toelichting = zaak.getToelichting();
        zaakData.zaaktype = ztcClientService.readZaaktype(zaak.getZaaktype()).getOmschrijving();
        zaakData.registratiedatum = zaak.getRegistratiedatum();
        zaakData.startdatum = zaak.getStartdatum();
        zaakData.einddatumGepland = zaak.getEinddatumGepland();
        zaakData.uiterlijkeEinddatumAfdoening = zaak.getUiterlijkeEinddatumAfdoening();
        zaakData.einddatum = zaak.getEinddatum();

        if (zaak.getStatus() != null) {
            zaakData.status = ztcClientService.readStatustype(
                    zrcClientService.readStatus(zaak.getStatus()).getStatustype()).getOmschrijving();
        }

        if (zaak.getResultaat() != null) {
            zaakData.resultaat = ztcClientService.readResultaattype(
                            zrcClientService.readResultaat(zaak.getResultaat()).getResultaattype())
                    .getOmschrijving();
        }

        if (zaak.isOpgeschort()) {
            zaakData.opschortingReden = zaak.getOpschorting().getReden();
        }

        if (zaak.isVerlengd()) {
            zaakData.verlengingReden = zaak.getVerlenging().getReden();
        }

        if (zaak.getVertrouwelijkheidaanduiding() != null) {
            zaakData.vertrouwelijkheidaanduiding = zaak.getVertrouwelijkheidaanduiding().toValue();
        }

        zgwApiService.findGroepForZaak(zaak)
                .map(RolOrganisatorischeEenheid::getNaam)
                .ifPresent(groep -> zaakData.groep = groep);

        zgwApiService.findBehandelaarForZaak(zaak)
                .map(RolMedewerker::getNaam)
                .ifPresent(behandelaar -> zaakData.behandelaar = behandelaar);

        if (zaak.getCommunicatiekanaal() != null) {
            vrlClientService.findCommunicatiekanaal(uuidFromURI(zaak.getCommunicatiekanaal()))
                    .map(CommunicatieKanaal::getNaam)
                    .ifPresent(communicatiekanaal -> zaakData.communicatiekanaal = communicatiekanaal);
        }

        return zaakData;
    }

    private AanvragerData createAanvragerData(final Zaak zaak) {
        return zgwApiService.findInitiatorForZaak(zaak)
                .map(this::convertToAanvragerData)
                .orElse(null);
    }

    private AanvragerData convertToAanvragerData(final Rol<?> initiator) {
        return switch (initiator.getBetrokkeneType()) {
            case NATUURLIJK_PERSOON -> createAanvragerDataNatuurlijkPersoon(initiator.getIdentificatienummer());
            case VESTIGING -> createAanvragerDataVestiging(initiator.getIdentificatienummer());
            case NIET_NATUURLIJK_PERSOON ->
                    createAanvragerDataNietNatuurlijkPersoon(initiator.getIdentificatienummer());
            default -> throw new NotImplementedException(String.format("Initiator of type '%s' is not supported"),
                                                         initiator.getBetrokkeneType().toValue());
        };
    }

    private AanvragerData createAanvragerDataNatuurlijkPersoon(final String bsn) {
        return brpClientService.findPersoon(bsn, FIELDS_PERSOON)
                .map(this::convertToAanvragerDataPersoon)
                .orElse(null);
    }

    private AanvragerData convertToAanvragerDataPersoon(final IngeschrevenPersoonHal persoon) {
        final AanvragerData aanvragerData = new AanvragerData();
        if (persoon.getNaam() != null) {
            aanvragerData.naam = convertToNaam(persoon.getNaam());
        }
        if (persoon.getVerblijfplaats() != null) {
            final Verblijfplaats verblijfplaats = persoon.getVerblijfplaats();
            aanvragerData.straat = verblijfplaats.getStraat();
            aanvragerData.huisnummer = convertToHuisnummer(verblijfplaats);
            aanvragerData.postcode = verblijfplaats.getPostcode();
            aanvragerData.woonplaats = verblijfplaats.getWoonplaats();
        }
        return aanvragerData;
    }

    private String convertToNaam(final NaamPersoon naam) {
        return Stream.of(naam.getVoornamen(), naam.getVoorvoegsel(), naam.getGeslachtsnaam())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
    }

    private String convertToHuisnummer(final Verblijfplaats verblijfplaats) {
        return Stream.of(Objects.toString(verblijfplaats.getHuisnummer(), null),
                         verblijfplaats.getHuisnummertoevoeging(), verblijfplaats.getHuisletter())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
    }

    private AanvragerData createAanvragerDataVestiging(final String vestigingsnummer) {
        return kvkClientService.findVestiging(vestigingsnummer)
                .map(this::convertToAanvragerDataBedrijf)
                .orElse(null);
    }

    private AanvragerData createAanvragerDataNietNatuurlijkPersoon(final String rsin) {
        return kvkClientService.findRechtspersoon(rsin)
                .map(this::convertToAanvragerDataBedrijf)
                .orElse(null);
    }

    private AanvragerData convertToAanvragerDataBedrijf(final ResultaatItem vestiging) {
        final AanvragerData aanvragerData = new AanvragerData();
        aanvragerData.naam = vestiging.getHandelsnaam();
        aanvragerData.straat = vestiging.getStraatnaam();
        aanvragerData.huisnummer = convertToHuisnummer(vestiging);
        aanvragerData.postcode = vestiging.getPostcode();
        aanvragerData.woonplaats = vestiging.getPlaats();
        return aanvragerData;
    }

    private String convertToHuisnummer(final ResultaatItem vestiging) {
        return Stream.of(Objects.toString(vestiging.getHuisnummer(), null), vestiging.getHuisnummerToevoeging())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
    }

    private StartformulierData createStartformulierData(final URI zaak) {
        final ZaakobjectListParameters listParameters = new ZaakobjectListParameters();
        listParameters.setZaak(zaak);
        listParameters.setObjectType(OVERIGE);
        return zrcClientService.listZaakobjecten(listParameters).getResults().stream()
                .filter(zo -> OBJECT_TYPE_OVERIGE_PRODUCTAANVRAAG.equals(zo.getObjectTypeOverige()))
                .findAny()
                .map(this::convertToStartformulierData)
                .orElse(null);
    }

    private StartformulierData convertToStartformulierData(final Zaakobject zaakobject) {
        final var productAaanvraagObject = objectsClientService.readObject(getUUID(zaakobject.getObject()));
        final var productAanvraag = productaanvraagService.getProductaanvraag(productAaanvraagObject);
        final var startformulierData = new StartformulierData();
        startformulierData.productAanvraagtype = productAanvraag.getType();
        startformulierData.data = productaanvraagService.getFormulierData(productAaanvraagObject);
        return startformulierData;
    }

    private TaakData createTaakData(final String taskId) {
        final TaakData taakData = new TaakData();
        final TaskInfo taskInfo = takenService.readTask(taskId);
        taakData.naam = taskInfo.getName();
        if (taskInfo.getAssignee() != null) {
            taakData.behandelaar = identityService.readUser(taskInfo.getAssignee()).getFullName();
        }
        taakData.data = taakVariabelenService.readTaakdata(taskInfo);
        return taakData;
    }
}
