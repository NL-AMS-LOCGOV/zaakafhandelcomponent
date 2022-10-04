/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag;

import static net.atos.client.zgw.zrc.model.Objecttype.ADRES;
import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.atos.client.bag.BAGClientService;
import net.atos.client.bag.model.AdresHal;
import net.atos.client.bag.model.AdresHalCollectie;
import net.atos.client.bag.model.AdresHalCollectieEmbedded;
import net.atos.client.bag.model.RaadpleegAdressenParameters;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;
import net.atos.zac.app.bag.model.BAGObjectType;
import net.atos.zac.app.bag.model.RESTAdres;
import net.atos.zac.app.bag.model.RESTBAGObjectGegevens;
import net.atos.zac.app.bag.model.RESTListAdressenParameters;
import net.atos.zac.app.shared.RESTResultaat;
import net.atos.zac.policy.PolicyService;

@Path("bag")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class BAGRESTService {

    @Inject
    private BAGClientService bagClientService;

    @Inject
    private ZRCClientService zrcClientService;

    @Inject
    private PolicyService policyService;

    @PUT
    @Path("adres")
    public RESTResultaat<RESTAdres> listAdressen(final RESTListAdressenParameters listAdressenParameters) {
        final RaadpleegAdressenParameters raadpleegAdressenParameters = new RaadpleegAdressenParameters();
        raadpleegAdressenParameters.setPostcode(listAdressenParameters.postcode);
        raadpleegAdressenParameters.setHuisnummer(listAdressenParameters.huisnummer);
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
        assertPolicy(policyService.readZaakActies(zaak).getToevoegenBAGObject());
        final Zaakobject zaakobject = new Zaakobject();
        zaakobject.setZaak(zaak.getUrl());
        zaakobject.setObject(bagObjectGegevens.bagObject);
        setObjecttype(bagObjectGegevens.bagObjecttype, zaakobject);
        zrcClientService.createZaakobject(zaakobject);
    }

    @GET
    @Path("/adres/zaak/{uuid}")
    public List<RESTAdres> listAdressenVoorZaak(@PathParam("uuid") final UUID zaakUUID) {
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakActies(zaak).getLezen());
        zaakobjectListParameters.setZaak(zaak.getUrl());
        final Results<Zaakobject> zaakobjecten = zrcClientService.listZaakobjecten(zaakobjectListParameters);
        if (zaakobjecten.getCount() > 0) {
            return zaakobjecten.getResults().stream()
                    .filter(zaakobject -> zaakobject.getObjectType() == ADRES)
                    .map(zaakobject -> bagClientService.readAdres(zaakobject.getObject()))
                    .map(this::convertToREST)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    private void setObjecttype(final BAGObjectType bagObjecttype, final Zaakobject zaakobject) {
        switch (bagObjecttype) {
            case ADRES -> zaakobject.setObjectType(ADRES);
        }
    }

    private RESTAdres convertToREST(final AdresHal adresHal) {
        final RESTAdres restAdres = new RESTAdres();
        restAdres.url = URI.create(adresHal.getLinks().getSelf().getHref());
        restAdres.postcode = adresHal.getPostcode();
        restAdres.huisnummer = convertToVolledigHuisnummer(adresHal);
        restAdres.straat = adresHal.getStraat();
        restAdres.woonplaats = adresHal.getWoonplaats();
        return restAdres;
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
