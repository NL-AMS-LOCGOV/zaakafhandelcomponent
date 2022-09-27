/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import static net.atos.client.or.shared.util.URIUtil.getUUID;
import static net.atos.client.zgw.zrc.model.Objecttype.OVERIGE;
import static net.atos.zac.configuratie.ConfiguratieService.BRON_ORGANISATIE;
import static net.atos.zac.configuratie.ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
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
import net.atos.client.zgw.zrc.model.RolVestiging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.zac.identity.IdentityService;
import net.atos.zac.identity.model.Group;
import net.atos.zac.identity.model.User;
import net.atos.zac.zaaksturing.ZaakafhandelParameterService;
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters;

@ApplicationScoped
public class ProductAanvraagService {

    public static final String ZAAK_INFORMATIEOBJECT_TITEL = "Aanvraag PDF";

    public static final String OBJECT_TYPE_OVERIGE_PRODUCT_AANVRAAG = "ProductAanvraag";

    private static final Logger LOG = Logger.getLogger(ProductAanvraagService.class.getName());

    private static final String ZAAK_INFORMATIEOBJECT_BESCHRIJVING = "PDF document met de aanvraag gegevens van de zaak";

    private static final String ZAAK_INFORMATIEOBJECT_REDEN = "Aanvraag document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag";

    private static final String ROL_TOELICHTING = "Initiator";

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

    public void verwerkProductAanvraag(final URI productAanvraagUrl) {
        final ORObject object = objectsClientService.readObject(getUUID(productAanvraagUrl));
        final ProductAanvraag productAanvraag = new ProductAanvraag(object.getRecord().getData());
        final CommunicatieKanaal communicatieKanaal = vrlClientService.findCommunicatiekanaal(COMMUNICATIEKANAAL_EFORMULIER);

        final UUID zaaktypeUUID = zaakafhandelParameterService.findZaaktypeUUIDByProductaanvraagType(productAanvraag.getType());
        if (zaaktypeUUID == null) {
            LOG.warning(String.format("Er is geen zaaktype gevonden voor productaanvraag type: '%s'. Er wordt geen zaak aangemaakt.",
                                      productAanvraag.getType()));
            return;
        }

        Zaak zaak = new Zaak();
        zaak.setZaaktype(ztcClientService.readZaaktype(zaaktypeUUID).getUrl());
        zaak.setOmschrijving((String) productAanvraag.getData().get("naamEvenement"));
        zaak.setToelichting((String) productAanvraag.getData().get("omschrijvingEvenement"));

        zaak.setStartdatum(object.getRecord().getStartAt());
        zaak.setBronorganisatie(BRON_ORGANISATIE);
        zaak.setVerantwoordelijkeOrganisatie(BRON_ORGANISATIE);
        if (communicatieKanaal != null) {
            zaak.setCommunicatiekanaal(communicatieKanaal.getUrl());
        }

        zaak = zgwApiService.createZaak(zaak);

        final ZaakafhandelParameters zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(zaak);
        toekennenZaak(zaak, zaakafhandelParameters);

        pairProductAanvraagWithZaak(productAanvraagUrl, zaak.getUrl());
        pairAanvraagPDFWithZaak(productAanvraag, zaak.getUrl());
        if (productAanvraag.getBsn() != null || productAanvraag.getKvk() != null) {
            addInitiator(productAanvraag.getBsn(), productAanvraag.getKvk(), zaak.getUrl(), zaak.getZaaktype());
        }
    }

    private void addInitiator(final String bsn, final String kvkNummer, final URI zaak, final URI zaaktype) {
        if (bsn != null) {
            final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaaktype);
            final RolNatuurlijkPersoon rolNatuurlijkPersoon = new RolNatuurlijkPersoon(zaak, initiator.getUrl(), ROL_TOELICHTING, new NatuurlijkPersoon(bsn));
            zrcClientService.createRol(rolNatuurlijkPersoon);
        } else {
            final ResultaatItem hoofdvestiging = kvkClientService.findHoofdvestiging(kvkNummer);
            if (hoofdvestiging != null) {
                final Roltype initiator = ztcClientService.readRoltype(AardVanRol.INITIATOR, zaaktype);
                final RolVestiging rolVestiging = new RolVestiging(zaak, initiator.getUrl(), ROL_TOELICHTING,
                                                                   new net.atos.client.zgw.zrc.model.Vestiging(hoofdvestiging.getVestigingsnummer()));
                zrcClientService.createRol(rolVestiging);
            } else {
                LOG.warning(() -> String.format("Geen hoofdvestiging gevonden voor bedrijf met kvk nummer '%s'", kvkNummer));
            }
        }
    }

    private void pairProductAanvraagWithZaak(final URI productAanvraagUrl, final URI zaakUrl) {
        final Zaakobject zaakobject = new Zaakobject();
        zaakobject.setZaak(zaakUrl);
        zaakobject.setObject(productAanvraagUrl);
        zaakobject.setObjectType(OVERIGE);
        zaakobject.setObjectTypeOverige(OBJECT_TYPE_OVERIGE_PRODUCT_AANVRAAG);
        zrcClientService.createZaakobject(zaakobject);
    }

    private void pairAanvraagPDFWithZaak(final ProductAanvraag productAanvraag, final URI zaakUrl) {
        final ZaakInformatieobject zaakInformatieobject = new ZaakInformatieobject();
        zaakInformatieobject.setInformatieobject(productAanvraag.getPdfUrl());
        zaakInformatieobject.setZaak(zaakUrl);
        zaakInformatieobject.setTitel(ZAAK_INFORMATIEOBJECT_TITEL);
        zaakInformatieobject.setBeschrijving(ZAAK_INFORMATIEOBJECT_BESCHRIJVING);
        zrcClientService.createZaakInformatieobject(zaakInformatieobject, ZAAK_INFORMATIEOBJECT_REDEN);
    }

    private void toekennenZaak(final Zaak zaak, final ZaakafhandelParameters zaakafhandelParameters) {
        if (zaakafhandelParameters.getGroepID() != null) {
            LOG.info(String.format("Zaak %s: toegekend aan groep '%s'", zaak.getUuid(), zaakafhandelParameters.getGroepID()));
            zrcClientService.createRol(creeerRolGroep(zaakafhandelParameters.getGroepID(), zaak));
        }
        if (zaakafhandelParameters.getGebruikersnaamMedewerker() != null) {
            LOG.info(String.format("Zaak %s: toegekend aan behandelaar '%s'", zaak.getUuid(), zaakafhandelParameters.getGebruikersnaamMedewerker()));
            zrcClientService.createRol(creeerRolMedewerker(zaakafhandelParameters.getGebruikersnaamMedewerker(), zaak));
        }
    }

    private RolOrganisatorischeEenheid creeerRolGroep(final String groepID, final Zaak zaak) {
        final Group group = identityService.readGroup(groepID);
        final OrganisatorischeEenheid groep = new OrganisatorischeEenheid();
        groep.setIdentificatie(group.getId());
        groep.setNaam(group.getName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolOrganisatorischeEenheid(zaak.getUrl(), roltype.getUrl(), "groep", groep);
    }

    private RolMedewerker creeerRolMedewerker(final String behandelaarGebruikersnaam, final Zaak zaak) {
        final User user = identityService.readUser(behandelaarGebruikersnaam);
        final Medewerker medewerker = new Medewerker();
        medewerker.setIdentificatie(user.getId());
        medewerker.setVoorletters(user.getFirstName());
        medewerker.setAchternaam(user.getLastName());
        final Roltype roltype = ztcClientService.readRoltype(AardVanRol.BEHANDELAAR, zaak.getZaaktype());
        return new RolMedewerker(zaak.getUrl(), roltype.getUrl(), "behandelaar", medewerker);
    }

}
