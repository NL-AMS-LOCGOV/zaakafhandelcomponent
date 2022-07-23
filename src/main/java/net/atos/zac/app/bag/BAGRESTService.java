/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag;

import static net.atos.client.zgw.zrc.model.Objecttype.OVERIGE;

import java.net.URI;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.bag.BAGClientService;
import net.atos.client.bag.model.AdresHal;
import net.atos.client.bag.model.AdresHalCollectie;
import net.atos.client.bag.model.AdresHalCollectieEmbedded;
import net.atos.client.bag.model.RaadpleegAdressenParameters;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.zac.app.bag.model.BAGObjectType;
import net.atos.zac.app.bag.model.RESTBAGObjectGegevens;
import net.atos.zac.app.bag.model.RESTListNummeraanduidingenParameters;
import net.atos.zac.app.bag.model.RESTNummeraanduiding;
import net.atos.zac.app.shared.RESTResultaat;

@Path("bag")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class BAGRESTService {

    @Inject
    private BAGClientService bagClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @PUT
    @Path("nummeraanduidingen")
    public RESTResultaat<RESTNummeraanduiding> listNummeraanduidingen(final RESTListNummeraanduidingenParameters listNnummeraanduidingenParameters) {
        final RaadpleegAdressenParameters raadpleegAdressenParameters = new RaadpleegAdressenParameters();
        raadpleegAdressenParameters.setPostcode(listNnummeraanduidingenParameters.postcode);
        raadpleegAdressenParameters.setHuisnummer(listNnummeraanduidingenParameters.huisnummer);
        final AdresHalCollectie adresHalCollectie = bagClientService.raadpleegAdressen(raadpleegAdressenParameters);
        final AdresHalCollectieEmbedded embedded = adresHalCollectie.getEmbedded();
        if (embedded.getAdressen() != null) {
            return new RESTResultaat<>(embedded.getAdressen().stream()
                                               .map(this::convertToREST)
                                               .toList());
        } else {
            return new RESTResultaat<>(Collections.emptyList());
        }
    }

    @POST
    @Path("")
    public void createBAGObject(final RESTBAGObjectGegevens bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        final Zaakobject zaakobject = new Zaakobject();
        zaakobject.setZaak(zaak.getUrl());
        zaakobject.setObject(bagObjectGegevens.bagObject);
        setObjecttype(bagObjectGegevens.bagObjecttype, zaakobject);
        zrcClientService.createZaakobject(zaakobject);
    }

    private void setObjecttype(final BAGObjectType bagObjecttype, final Zaakobject zaakobject) {
        switch (bagObjecttype) {
            case NUMMERAANDUIDING -> {
                zaakobject.setObjectType(OVERIGE);
                zaakobject.setObjectTypeOverige("nummeraanduiding");
            }
        }
        ;
    }

    private RESTNummeraanduiding convertToREST(final AdresHal adresHal) {
        final RESTNummeraanduiding nummeraanduiding = new RESTNummeraanduiding();
        nummeraanduiding.url = URI.create(adresHal.getLinks().getNummeraanduiding().getHref());
        nummeraanduiding.postcode = adresHal.getPostcode();
        nummeraanduiding.huisnummer = convertToVolledigHuisnummer(adresHal);
        nummeraanduiding.straat = adresHal.getStraat();
        nummeraanduiding.woonplaats = adresHal.getWoonplaats();
        return nummeraanduiding;
    }

    private String convertToVolledigHuisnummer(final AdresHal adresHal) {
        final StringBuilder volledigHuisnummer = new StringBuilder();
        if (adresHal.getHuisnummer() != null) {
            volledigHuisnummer.append(adresHal.getHuisnummer());
        }
        if (adresHal.getHuisletter() != null) {
            volledigHuisnummer.append(adresHal.getHuisletter());
        }
        if (adresHal.getHuisnummertoevoeging() != null) {
            volledigHuisnummer.append(adresHal.getHuisnummertoevoeging());
        }
        return volledigHuisnummer.toString().trim();
    }
}
