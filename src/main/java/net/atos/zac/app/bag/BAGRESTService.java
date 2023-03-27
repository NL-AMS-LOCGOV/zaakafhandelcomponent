/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag;

import static net.atos.client.zgw.zrc.model.Objecttype.ADRES;
import static net.atos.zac.policy.PolicyService.assertPolicy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import net.atos.client.bag.model.BevraagAdressenParameters;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.zrc.ZRCClientService;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectAdres;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectOpenbareRuimte;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectPand;
import net.atos.client.zgw.zrc.model.zaakobjecten.ObjectWoonplaats;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectAdres;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectListParameters;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectOpenbareRuimte;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectPand;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectWoonplaats;
import net.atos.zac.app.bag.converter.RESTAdresConverter;
import net.atos.zac.app.bag.model.BAGObjectType;
import net.atos.zac.app.bag.model.RESTAdres;
import net.atos.zac.app.bag.model.RESTBAGObjectGegevens;
import net.atos.zac.app.bag.model.RESTListAdressenParameters;
import net.atos.zac.app.bag.model.RESTNummeraanduiding;
import net.atos.zac.app.bag.model.RESTOpenbareRuimte;
import net.atos.zac.app.bag.model.RESTPand;
import net.atos.zac.app.bag.model.RESTWoonplaats;
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
    private RESTAdresConverter adresConverter;

    @Inject
    private PolicyService policyService;

    @PUT
    @Path("adres")
    public RESTResultaat<RESTAdres> listAdressen(final RESTListAdressenParameters listAdressenParameters) {
        final BevraagAdressenParameters bevraagAdressenParameters = new BevraagAdressenParameters();
        bevraagAdressenParameters.setQ(listAdressenParameters.trefwoorden);
        bevraagAdressenParameters.setExpand(getExpand(BAGObjectType.NUMMERAANDUIDING, BAGObjectType.OPENBARE_RUIMTE, BAGObjectType.PAND,
                                                      BAGObjectType.WOONPLAATS));
        return new RESTResultaat<>(bagClientService.listAdressen(bevraagAdressenParameters).stream()
                                           .map(adres -> adresConverter.convertToREST(adres))
                                           .toList());
    }

    @POST
    @Path("ADRES")
    public void createAdres(final RESTBAGObjectGegevens<RESTAdres> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final RESTAdres adres = bagObjectGegevens.bagObject;
        ObjectAdres objectAdres = new ObjectAdres(adres.identificatie, adres.woonplaatsNaam, adres.openbareRuimteNaam, adres.huisnummer, adres.huisletter,
                                                  adres.huisnummertoevoeging, adres.postcode);
        final ZaakobjectAdres zaakobject = new ZaakobjectAdres(zaak.getUrl(), adres.url, objectAdres);
        zrcClientService.createZaakobject(zaakobject);
    }

    @POST
    @Path("WOONPLAATS")
    public void createWoonplaats(final RESTBAGObjectGegevens<RESTWoonplaats> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final RESTWoonplaats woonplaats = bagObjectGegevens.bagObject;
        final ZaakobjectWoonplaats zaakobject = new ZaakobjectWoonplaats(zaak.getUrl(), woonplaats.url,
                                                                         new ObjectWoonplaats(woonplaats.identificatie, woonplaats.naam));
        zrcClientService.createZaakobject(zaakobject);
    }

    @POST
    @Path("OPENBARE_RUIMTE")
    public void createOpenbareRuimte(final RESTBAGObjectGegevens<RESTOpenbareRuimte> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final RESTOpenbareRuimte openbareRuimte = bagObjectGegevens.bagObject;
        final ObjectOpenbareRuimte objectOpenbareRuimte = new ObjectOpenbareRuimte(
                openbareRuimte.identificatie, openbareRuimte.naam, openbareRuimte.woonplaatsNaam);
        final ZaakobjectOpenbareRuimte zaakobject = new ZaakobjectOpenbareRuimte(zaak.getUrl(), openbareRuimte.url, objectOpenbareRuimte);
        zrcClientService.createZaakobject(zaakobject);
    }

    @POST
    @Path("PAND")
    public void createPand(final RESTBAGObjectGegevens<RESTPand> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        final RESTPand pand = bagObjectGegevens.bagObject;
        final ZaakobjectPand zaakobject = new ZaakobjectPand(zaak.getUrl(), pand.url, new ObjectPand(pand.identificatie));
        zrcClientService.createZaakobject(zaakobject);
    }

    @POST
    @Path("NUMMERAANDUIDING")
    public void createNummeraanduiding(final RESTBAGObjectGegevens<RESTNummeraanduiding> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        final RESTNummeraanduiding nummeraanduiding = bagObjectGegevens.bagObject;
        final ObjectNummeraanduiding objectNummeraanduiding = new ObjectNummeraanduiding(
                nummeraanduiding.identificatie,
                nummeraanduiding.huisnummer,
                nummeraanduiding.huisletter,
                nummeraanduiding.huisnummertoevoeging,
                nummeraanduiding.postcode,
                nummeraanduiding.typeAdresseerbaarObject != null ? nummeraanduiding.typeAdresseerbaarObject.toString() : null,
                nummeraanduiding.status != null ? nummeraanduiding.status.toString() : null
        );
        final ZaakobjectNummeraanduiding zaakobject = new ZaakobjectNummeraanduiding(zaak.getUrl(), nummeraanduiding.url, objectNummeraanduiding);
        zrcClientService.createZaakobject(zaakobject);
    }


    @GET
    @Path("/adres/zaak/{uuid}")
    public List<RESTAdres> listAdressenVoorZaak(@PathParam("uuid") final UUID zaakUUID) {
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getLezen());
        zaakobjectListParameters.setZaak(zaak.getUrl());
        final Results<Zaakobject> zaakobjecten = zrcClientService.listZaakobjecten(zaakobjectListParameters);
        if (zaakobjecten.getCount() > 0) {
            return zaakobjecten.getResults().stream()
                    .filter(zaakobject -> zaakobject.getObjectType() == ADRES)
                    .map(zaakobject -> bagClientService.readAdres(zaakobject.getObject()))
                    .map(adresConverter::convertToREST)
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

    private String getExpand(final BAGObjectType... bagObjectTypes) {
        return Arrays.stream(bagObjectTypes).map(BAGObjectType::getExpand).collect(Collectors.joining(","));
    }
}
