/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import static net.atos.zac.configuratie.ConfiguratieService.BRON_ORGANISATIE;
import static net.atos.zac.configuratie.ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER;
import static net.atos.zac.util.UriUtil.uuidFromURI;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.vrl.VRLClientService;
import net.atos.client.vrl.model.CommunicatieKanaal;
import net.atos.client.zgw.drc.DRCClientService;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
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
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectProductaanvraag;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.zac.aanvraag.model.InboxProductaanvraag;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.documenten.InboxDocumentenService;
import net.atos.zac.flowable.BPMNService;
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

    private static final Logger LOG = Logger.getLogger(ProductaanvraagService.class.getName());

    public static final String AANVRAAG_PDF_TITEL = "Aanvraag PDF";

    public static final String AANVRAAG_PDF_BESCHRIJVING = "PDF document met de aanvraag gegevens van de zaak";

    public static final String ZAAK_INFORMATIEOBJECT_REDEN = "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag";

    private static final String ROL_TOELICHTING = "Overgenomen vanuit de product aanvraag";

    private static final String PRODUCT_AANVRAAG_FORMULIER_DATA_VELD = "data";

    private static final String FORMULIER_KLEINE_EVENEMENTEN_MELDING_EIGENSCHAPNAAM_NAAM_EVENEMENT = "naamEvenement";

    private static final String FORMULIER_KLEINE_EVENEMENTEN_MELDING_EIGENSCHAPNAAM_OMSCHRIJVING_EVENEMENT =
            "omschrijvingEvenement";

    private static final String FORMULIER_VELD_ZAAK_TOELICHTING = "zaak_toelichting";

    private static final String FORMULIER_VELD_ZAAK_OMSCHRIJVING = "zaak_omschrijving";

    @Inject
    private ObjectsClientService objectsClientService;

    @Inject
    private ZGWApiService zgwApiService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private DRCClientService drcClientService;

    @Inject
    private ZTCClientService ztcClientService;

    @Inject
    private VRLClientService vrlClientService;

    @Inject
    private IdentityService identityService;

    @Inject
    private ZaakafhandelParameterService zaakafhandelParameterService;

    @Inject
    private ZaakafhandelParameterBeheerService zaakafhandelParameterBeheerService;

    @Inject
    private InboxDocumentenService inboxDocumentenService;

    @Inject
    private InboxProductaanvraagService inboxProductaanvraagService;

    @Inject
    private CMMNService cmmnService;

    @Inject
    private BPMNService bpmnService;

    @Inject
    private ConfiguratieService configuratieService;

    public void verwerkProductaanvraag(final URI productaanvraagUrl) {
        final var productaanvraagObject = objectsClientService.readObject(uuidFromURI(productaanvraagUrl));
        final var productaanvraag = getProductaanvraag(productaanvraagObject);
        final Optional<UUID> zaaktypeUUID = zaakafhandelParameterBeheerService.findZaaktypeUUIDByProductaanvraagType(
                productaanvraag.getType());
        if (zaaktypeUUID.isPresent()) {
            try {
                registreerZaakMetCMMNCase(zaaktypeUUID.get(), productaanvraag, productaanvraagObject);
            } catch (RuntimeException ex) {
                warning("CMMN", productaanvraag, ex);
            }
        } else {
            final var zaaktype = findZaaktypeByIdentificatie(productaanvraag.getType());
            if (zaaktype.isPresent()) {
                try {
                    registreerZaakMetBPMNProces(zaaktype.get(), productaanvraag, productaanvraagObject);
                } catch (RuntimeException ex) {
                    warning("BPMN", productaanvraag, ex);
                }
            } else {
                LOG.info(message(productaanvraag,
                                 "Er is geen zaaktype gevonden voor het type '%s'. Er wordt geen zaak aangemaakt."
                                         .formatted(productaanvraag.getType())));
                registreerInbox(productaanvraag, productaanvraagObject);
            }
        }
    }

    private void warning(final String type, final ProductaanvraagDenhaag productaanvraag, final RuntimeException ex) {
        LOG.log(Level.WARNING,
                message(productaanvraag, "Er is iets fout gegaan bij het aanmaken van een %s-zaak."
                        .formatted(type)), ex);
    }

    private String message(final ProductaanvraagDenhaag productaanvraag, final String message) {
        return "Productaanvraag %s: %s"
                .formatted(productaanvraag.getSubmissionId(), message);
    }

    private void registreerZaakMetBPMNProces(final Zaaktype zaaktype, final ProductaanvraagDenhaag productaanvraag,
            final ORObject productaanvraagObject) {
        final Map<String, Object> formulierData = getFormulierData(productaanvraagObject);
        var zaak = new Zaak();
        zaak.setZaaktype(zaaktype.getUrl());
        zaak.setBronorganisatie(BRON_ORGANISATIE);
        zaak.setVerantwoordelijkeOrganisatie(BRON_ORGANISATIE);
        zaak.setStartdatum(LocalDate.now());
        final var omschrijving = (String) formulierData.get(FORMULIER_VELD_ZAAK_OMSCHRIJVING);
        if (isNotBlank(omschrijving)) {
            zaak.setOmschrijving(omschrijving);
        }
        final var toelichting = (String) formulierData.get(FORMULIER_VELD_ZAAK_TOELICHTING);
        if (StringUtils.isNotBlank(toelichting)) {
            zaak.setToelichting(toelichting);
        }
        zaak = zgwApiService.createZaak(zaak);
        final var PROCESS_DEFINITION_KEY = "test-met-proces";
        final var processDefinition = bpmnService.readProcessDefinitionByprocessDefinitionKey(PROCESS_DEFINITION_KEY);
        zrcClientService.createRol(creeerRolGroep(processDefinition.getDescription(), zaak));
        pairProductaanvraagInfoWithZaak(productaanvraag, productaanvraagObject, zaak);
        bpmnService.startProcess(zaak, zaaktype, formulierData, PROCESS_DEFINITION_KEY);
    }

    public Map<String, Object> getFormulierData(final ORObject productaanvraagObject) {
        final Map<String, Object> formulierData = new HashMap<>();
        ((Map<String, Object>) productaanvraagObject.getRecord().getData().get(PRODUCT_AANVRAAG_FORMULIER_DATA_VELD))
                .forEach((stap, velden) -> formulierData.putAll((Map<String, Object>) velden));
        return formulierData;
    }

    public ProductaanvraagDenhaag getProductaanvraag(final ORObject productaanvraagObject) {
        return JsonbUtil.JSONB.fromJson(JsonbUtil.JSONB.toJson(productaanvraagObject.getRecord().getData()),
                                        ProductaanvraagDenhaag.class);
    }

    private void addInitiator(final String bsn, final URI zaak, final URI zaaktype) {
        final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaaktype);
        final RolNatuurlijkPersoon rolNatuurlijkPersoon = new RolNatuurlijkPersoon(zaak, initiator, ROL_TOELICHTING,
                                                                                   new NatuurlijkPersoon(bsn));
        zrcClientService.createRol(rolNatuurlijkPersoon);
    }

    private void registreerInbox(final ProductaanvraagDenhaag productaanvraag, final ORObject productaanvraagObject) {
        final InboxProductaanvraag inboxProductaanvraag = new InboxProductaanvraag();
        inboxProductaanvraag.setProductaanvraagObjectUUID(productaanvraagObject.getUuid());
        inboxProductaanvraag.setType(productaanvraag.getType());
        inboxProductaanvraag.setOntvangstdatum(productaanvraagObject.getRecord().getRegistrationAt());
        if (StringUtils.isNotBlank(productaanvraag.getBsn())) {
            inboxProductaanvraag.setInitiatorID(productaanvraag.getBsn());
        }
        if (productaanvraag.getPdfUrl() != null) {
            final UUID aanvraagDocumentUUID = uuidFromURI(productaanvraag.getPdfUrl());
            inboxProductaanvraag.setAanvraagdocumentUUID(aanvraagDocumentUUID);
            deleteInboxDocument(aanvraagDocumentUUID);
        }
        final List<URI> bijlagen = ListUtils.emptyIfNull(productaanvraag.getAttachments());
        inboxProductaanvraag.setAantalBijlagen(bijlagen.size());
        bijlagen.forEach(bijlage -> deleteInboxDocument(uuidFromURI(bijlage)));
        inboxProductaanvraagService.create(inboxProductaanvraag);
    }

    private void deleteInboxDocument(final UUID documentUUID) {
        inboxDocumentenService.find(documentUUID)
                .ifPresent(inboxDocument -> inboxDocumentenService.delete(inboxDocument.getId()));
    }

    private void registreerZaakMetCMMNCase(final UUID zaaktypeUuid, final ProductaanvraagDenhaag productaanvraag,
            final ORObject productaanvraagObject) {
        final var formulierData = getFormulierData(productaanvraagObject);
        var zaak = new Zaak();
        final var zaaktype = ztcClientService.readZaaktype(zaaktypeUuid);
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
                zaaktypeUuid);
        toekennenZaak(zaak, zaakafhandelParameters);
        pairProductaanvraagInfoWithZaak(productaanvraag, productaanvraagObject, zaak);
        cmmnService.startCase(zaak, zaaktype, zaakafhandelParameters, formulierData);
    }

    private void pairProductaanvraagInfoWithZaak(final ProductaanvraagDenhaag productaanvraag,
            final ORObject productaanvraagObject, final Zaak zaak) {
        pairProductaanvraagWithZaak(productaanvraagObject, zaak.getUrl());
        pairAanvraagPDFWithZaak(productaanvraag, zaak.getUrl());
        pairBijlagenWithZaak(productaanvraag.getAttachments(), zaak.getUrl());
        if (isNotBlank(productaanvraag.getBsn())) {
            addInitiator(productaanvraag.getBsn(), zaak.getUrl(), zaak.getZaaktype());
        }
    }

    public void pairProductaanvraagWithZaak(final ORObject productaanvraag, final URI zaakUrl) {
        final ZaakobjectProductaanvraag zaakobject = new ZaakobjectProductaanvraag(zaakUrl, productaanvraag.getUrl());
        zrcClientService.createZaakobject(zaakobject);
    }

    public void pairAanvraagPDFWithZaak(final ProductaanvraagDenhaag productaanvraag, final URI zaakUrl) {
        final ZaakInformatieobject zaakInformatieobject = new ZaakInformatieobject();
        zaakInformatieobject.setInformatieobject(productaanvraag.getPdfUrl());
        zaakInformatieobject.setZaak(zaakUrl);
        zaakInformatieobject.setTitel(AANVRAAG_PDF_TITEL);
        zaakInformatieobject.setBeschrijving(AANVRAAG_PDF_BESCHRIJVING);
        zrcClientService.createZaakInformatieobject(zaakInformatieobject, ZAAK_INFORMATIEOBJECT_REDEN);
    }

    public void pairBijlagenWithZaak(final List<URI> bijlageURIs, final URI zaakUrl) {
        for (final URI bijlageURI : ListUtils.emptyIfNull(bijlageURIs)) {
            final EnkelvoudigInformatieobject bijlage = drcClientService.readEnkelvoudigInformatieobject(bijlageURI);
            final ZaakInformatieobject zaakInformatieobject = new ZaakInformatieobject();
            zaakInformatieobject.setInformatieobject(bijlage.getUrl());
            zaakInformatieobject.setZaak(zaakUrl);
            zaakInformatieobject.setTitel(bijlage.getTitel());
            zaakInformatieobject.setBeschrijving(bijlage.getBeschrijving());
            zrcClientService.createZaakInformatieobject(zaakInformatieobject, ZAAK_INFORMATIEOBJECT_REDEN);
        }
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

    private Optional<Zaaktype> findZaaktypeByIdentificatie(final String zaaktypeIdentificatie) {
        return ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI()).stream()
                .filter(zaak -> zaak.getIdentificatie().equals(zaaktypeIdentificatie))
                .findFirst();
    }
}
