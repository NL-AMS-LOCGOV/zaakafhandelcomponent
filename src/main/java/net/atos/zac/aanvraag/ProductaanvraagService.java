/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import static net.atos.client.zgw.zrc.model.Objecttype.OVERIGE;
import static net.atos.zac.configuratie.ConfiguratieService.BRON_ORGANISATIE;
import static net.atos.zac.configuratie.ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.atos.client.kvk.KVKClientService;
import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Medewerker;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.RolMedewerker;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.flowable.CMMNService;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.util.JsonbUtil;
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@ApplicationScoped
public class ProductaanvraagService {

    public static final String OBJECT_TYPE_OVERIGE_PRODUCTAANVRAAG = "ProductAanvraag";

    private static final Logger LOG = Logger.getLogger(ProductaanvraagService.class.getName());

    private static final String ZAAK_INFORMATIEOBJECT_TITEL = "Aanvraag PDF";

    private static final String ZAAK_INFORMATIEOBJECT_BESCHRIJVING = "PDF document met de aanvraag gegevens van de zaak";

    private static final String ZAAK_INFORMATIEOBJECT_REDEN = "Aanvraag document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag";

    private static final String ROL_TOELICHTING = "Overgenomen vanuit de product aanvraag";

    private static final String FORMULIER_DATA = "data";

    private static final String FORMULIER_KLEINE_EVENEMENTEN_MELDING_EIGENSCHAPNAAM_NAAM_EVENEMENT = "naamEvenement";

    private static final String FORMULIER_KLEINE_EVENEMENTEN_MELDING_EIGENSCHAPNAAM_OMSCHRIJVING_EVENEMENT =
            "omschrijvingEvenement";

    @Inject
    private ObjectsClientService objectsClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private KVKClientService kvkClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private IdentityService identityService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private CMMNService cmmnService;

    public void verwerkProductaanvraag(final URI productaanvraagUrl) {
        final var productaanvraagObject = objectsClientService.readObject(uuidFromURI(productaanvraagUrl));
        final var productaanvraag = getProductaanvraag(productaanvraagObject);
        final var formulierData = getFormulierData(productaanvraagObject);

        final Optional<UUID> zaaktypeUUID = zaakafhandelParameterBeheerService.findZaaktypeUUIDByProductaanvraagType(
                productaanvraag.getType());
        if (zaaktypeUUID.isEmpty()) {
            LOG.warning(String.format(
                    "Er is geen zaaktype gevonden voor productaanvraag type: '%s'. Er wordt geen zaak aangemaakt.",
                    productaanvraag.getType()));
            return;
        }

        var zaak = new Zaak();
        final var zaaktype = ztcClientService.readZaaktype(zaaktypeUUID.get());
        zaak.setZaaktype(zaaktype.getUrl());
        zaak.setOmschrijving(
                (String) formulierData.get(FORMULIER_KLEINE_EVENEMENTEN_MELDING_EIGENSCHAPNAAM_NAAM_EVENEMENT));
        zaak.setToelichting(
                (String) formulierData.get(FORMULIER_KLEINE_EVENEMENTEN_MELDING_EIGENSCHAPNAAM_OMSCHRIJVING_EVENEMENT));
        zaak.setStartdatum(productaanvraagObject.getRecord().getStartAt());
        zaak.setBronorganisatie(BRON_ORGANISATIE);
        zaak.setVerantwoordelijkeOrganisatie(BRON_ORGANISATIE);
        final Optional<CommunicatieKanaal> communicatiekanaal = vrlClientService.findCommunicatiekanaal(
                COMMUNICATIEKANAAL_EFORMULIER);
        if (communicatiekanaal.isPresent()) {
            zaak.setCommunicatiekanaal(communicatiekanaal.get().getUrl());
        }

        zaak = zgwApiService.createZaak(zaak);

        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(
                zaaktypeUUID.get());
        toekennenZaak(zaak, zaakafhandelParameters);

        pairProductaanvraagWithZaak(productaanvraagUrl, zaak.getUrl());
        pairAanvraagPDFWithZaak(productaanvraag, zaak.getUrl());
        if (isNotBlank(productaanvraag.getBsn())) {
            addInitiator(productaanvraag.getBsn(), zaak.getUrl(), zaak.getZaaktype());
        }

        cmmnService.startCase(zaak, zaaktype, zaakafhandelParameters, formulierData);
    }

    public ProductaanvraagDenhaag getProductaanvraag(final ORObject productaanvraagObject) {
        return JsonbUtil.JSONB.fromJson(JsonbUtil.JSONB.toJson(productaanvraagObject.getRecord().getData()),
                                        ProductaanvraagDenhaag.class);
    }

    public Map<String, Object> getFormulierData(final ORObject productaanvraagObject) {
        return (Map<String, Object>) productaanvraagObject.getRecord().getData().get(FORMULIER_DATA);
    }

    private void addInitiator(final String bsn, final URI zaak, final URI zaaktype) {
        final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaaktype);
        final RolNatuurlijkPersoon rolNatuurlijkPersoon = new RolNatuurlijkPersoon(zaak, initiator, ROL_TOELICHTING,
                                                                                   new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rolNatuurlijkPersoon);
    }

    private void pairProductaanvraagWithZaak(final URI productaanvraagUrl, final URI zaakUrl) {
        final Zaakobject zaakobject = new Zaakobject();
        zaakobject.setZaak(zaakUrl);
        zaakobject.setObject(productaanvraagUrl);
        zaakobject.setObjectType(OVERIGE);
        zaakobject.setObjectTypeOverige(OBJECT_TYPE_OVERIGE_PRODUCTAANVRAAG);
        zrcClientService.createZaakobject(zaakobject);
    }

    private void pairAanvraagPDFWithZaak(final ProductaanvraagDenhaag productaanvraag, final URI zaakUrl) {
        final ZaakInformatieobject zaakInformatieobject = new ZaakInformatieobject();
        zaakInformatieobject.setInformatieobject(productaanvraag.getPdfUrl());
        zaakInformatieobject.setZaak(zaakUrl);
        zaakInformatieobject.setTitel(ZAAK_INFORMATIEOBJECT_TITEL);
        zaakInformatieobject.setBeschrijving(ZAAK_INFORMATIEOBJECT_BESCHRIJVING);
        zrcClientService.createZaakInformatieobject(zaakInformatieobject, ZAAK_INFORMATIEOBJECT_REDEN);
    }

    private void toekennenZaak(final Zaak zaak, final ZaakafhandelParameters zaakafhandelParameters) {
        if (zaakafhandelParameters.getGroepID() != null) {
            LOG.info(String.format("Zaak %s: toegekend aan groep '%s'", zaak.getUuid(),
                                   zaakafhandelParameters.getGroepID()));
            zrcClientService.createRol(creeerRolGroep(zaakafhandelParameters.getGroepID(), zaak));
        }
        if (zaakafhandelParameters.getGebruikersnaamMedewerker() != null) {
            LOG.info(String.format("Zaak %s: toegekend aan behandelaar '%s'", zaak.getUuid(),
                                   zaakafhandelParameters.getGebruikersnaamMedewerker()));
            zrcClientService.createRol(creeerRolMedewerker(zaakafhandelParameters.getGebruikersnaamMedewerker(), zaak));
        }
    }

    private RolOrganisatorischeEenheid creeerRolGroep(final String groepID, final Zaak zaak) {
        final Group group = identityService.readGroup(groepID);
        final OrganisatorischeEenheid groep = new OrganisatorischeEenheid();
        groep.setIdentificatie(group.getId());
        groep.setNaam(group.getName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolOrganisatorischeEenheid(zaak.getUrl(), roltype, "Behandelend groep van de zaak", groep);
    }

    private RolMedewerker creeerRolMedewerker(final String behandelaarGebruikersnaam, final Zaak zaak) {
        final User user = identityService.readUser(behandelaarGebruikersnaam);
        final Medewerker medewerker = new Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setVoorletters(user.getFirstName());
        medewerker.setAchternaam(user.getLastName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolMedewerker(zaak.getUrl(), roltype, "Behandelaar van de zaak", medewerker);
    }
}
