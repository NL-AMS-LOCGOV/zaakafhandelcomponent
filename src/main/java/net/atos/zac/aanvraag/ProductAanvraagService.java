/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.aanvraag;

import static net.atos.client.or.shared.util.URIUtil.getUUID;
import static net.atos.client.zgw.zrc.model.Objecttype.OVERIGE;
import static net.atos.zac.configuratie.ConfiguratieService.BRON_ORGANISATIE;
import static net.atos.zac.configuratie.ConfiguratieService.MELDING_KLEIN_EVENEMENT_ZAAKTYPE_IDENTIFICATIE;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.atos.client.kvk.KVKClientService;
import net.atos.client.kvk.zoeken.model.ResultaatItem;
import net.atos.client.or.object.ObjectsClientService;
import net.atos.client.or.object.model.ORObject;
import net.atos.client.zgw.shared.ZGWApiService;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon;
import net.atos.client.zgw.zrc.model.RolVestiging;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieobject;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.ztc.ZTCClientService;
import net.atos.client.zgw.ztc.model.AardVanRol;
import net.atos.client.zgw.ztc.model.Roltype;

@ApplicationScoped
public class ProductAanvraagService {

    private static final Logger LOG = Logger.getLogger(ProductAanvraagService.class.getName());

    private static final String OBJECT_TYPE_OVERIGE_PRODUCT_AANVRAAG = "ProductAanvraag";

    private static final String ZAAK_INFORMATIEOBJECT_TITEL = "Aanvraag PDF";

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

    public void createZaak(final URI productAanvraagUrl) {
        final ORObject object = objectsClientService.readObject(getUUID(productAanvraagUrl));
        final ProductAanvraag productAanvraag = new ProductAanvraag(object.getRecord().getData());

        Zaak zaak;
        switch (productAanvraag.getType()) {
            case "melding_klein_evenement":
                zaak = createMeldingKleinEvenement(productAanvraag.getData());
                break;
            default:
                LOG.warning(String.format("Onbekend product aanvraag type '%s'", productAanvraag.getType()));
                return;
        }
        zaak.setStartdatum(object.getRecord().getStartAt());
        zaak.setBronorganisatie(BRON_ORGANISATIE);
        zaak.setVerantwoordelijkeOrganisatie(BRON_ORGANISATIE);
        zaak = zgwApiService.createZaak(zaak);

        pairProductAanvraagWithZaak(productAanvraagUrl, zaak.getUrl());
        pairAanvraagPDFWithZaak(productAanvraag, zaak.getUrl());
        if (productAanvraag.getBsn() != null || productAanvraag.getKvk() != null) {
            addInitiator(productAanvraag.getBsn(), productAanvraag.getKvk(), zaak.getUrl(), zaak.getZaaktype());
        }
    }

    private void addInitiator(final String bsn, final String kvkNummer, final URI zaak, final URI zaaktype) {
        if (bsn != null) {
            final Roltype initiator = ztcClientService.readRoltype(zaaktype, AardVanRol.INITIATOR);
            final RolNatuurlijkPersoon rolNatuurlijkPersoon = new RolNatuurlijkPersoon(zaak, initiator.getUrl(), ROL_TOELICHTING, new NatuurlijkPersoon(bsn));
            zrcClientService.createRol(rolNatuurlijkPersoon);
        } else {
            final ResultaatItem hoofdvestiging = kvkClientService.findHoofdvestiging(kvkNummer);
            if (hoofdvestiging != null) {
                final Roltype initiator = ztcClientService.readRoltype(zaaktype, AardVanRol.INITIATOR);
                final RolVestiging rolVestiging = new RolVestiging(zaak, initiator.getUrl(), ROL_TOELICHTING,
                                                                   new net.atos.client.zgw.zrc.model.Vestiging(hoofdvestiging.getVestigingsnummer()));
                zrcClientService.createRol(rolVestiging);
            } else {
                LOG.warning(() -> String.format("Geen hoofdvestiging gevonden voor bedrijf met kvk nummer '%s'", kvkNummer));
            }
        }
    }

    private Zaak createMeldingKleinEvenement(final Map<String, Object> aanvraagData) {
        final Zaak zaak = new Zaak();
        zaak.setZaaktype(ztcClientService.readZaaktypeUrl(MELDING_KLEIN_EVENEMENT_ZAAKTYPE_IDENTIFICATIE));
        zaak.setOmschrijving((String) aanvraagData.get("naamEvenement"));
        zaak.setToelichting((String) aanvraagData.get("omschrijvingEvenement"));
        return zaak;
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
}
