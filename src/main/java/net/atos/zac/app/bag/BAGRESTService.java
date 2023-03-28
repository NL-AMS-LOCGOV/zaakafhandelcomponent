/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.bag;

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
import net.atos.client.zgw.zrc.model.Objecttype;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.zaakobjecten.Zaakobject;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectAdres;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectListParameters;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectNummeraanduiding;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectOpenbareRuimte;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectPand;
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectWoonplaats;
import net.atos.zac.app.bag.converter.RESTAdresConverter;
import net.atos.zac.app.bag.converter.RESTNummeraanduidingConverter;
import net.atos.zac.app.bag.converter.RESTOpenbareRuimteConverter;
import net.atos.zac.app.bag.converter.RESTPandConverter;
import net.atos.zac.app.bag.converter.RESTWoonplaatsConverter;
import net.atos.zac.app.bag.model.BAGObjectType;
import net.atos.zac.app.bag.model.RESTAdres;
import net.atos.zac.app.bag.model.RESTBAGObject;
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
    private RESTNummeraanduidingConverter nummeraanduidingConverter;

    @Inject
    private RESTOpenbareRuimteConverter openbareRuimteConverter;

    @Inject
    private RESTPandConverter pandConverter;

    @Inject
    private RESTWoonplaatsConverter woonplaatsConverter;

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
        if (isNogNietGekoppeld(bagObjectGegevens.bagObject, zaak)) {
            zrcClientService.createZaakobject(adresConverter.convertToZaakobject(bagObjectGegevens, zaak));
        }
    }

    @POST
    @Path("WOONPLAATS")
    public void createWoonplaats(final RESTBAGObjectGegevens<RESTWoonplaats> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        if (isNogNietGekoppeld(bagObjectGegevens.bagObject, zaak)) {
            zrcClientService.createZaakobject(woonplaatsConverter.convertToZaakobject(bagObjectGegevens, zaak));
        }
    }

    @POST
    @Path("OPENBARE_RUIMTE")
    public void createOpenbareRuimte(final RESTBAGObjectGegevens<RESTOpenbareRuimte> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        if (isNogNietGekoppeld(bagObjectGegevens.bagObject, zaak)) {
            zrcClientService.createZaakobject(openbareRuimteConverter.convertToZaakobject(bagObjectGegevens, zaak));
        }
    }

    @POST
    @Path("PAND")
    public void createPand(final RESTBAGObjectGegevens<RESTPand> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        if (isNogNietGekoppeld(bagObjectGegevens.bagObject, zaak)) {
            zrcClientService.createZaakobject(pandConverter.convertToZaakobject(bagObjectGegevens, zaak));
        }
    }

    @POST
    @Path("NUMMERAANDUIDING")
    public void createNummeraanduiding(final RESTBAGObjectGegevens<RESTNummeraanduiding> bagObjectGegevens) {
        final Zaak zaak = zrcClientService.readZaak(bagObjectGegevens.zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getBehandelen());
        if (isNogNietGekoppeld(bagObjectGegevens.bagObject, zaak)) {
            zrcClientService.createZaakobject(nummeraanduidingConverter.convertToZaakobject(bagObjectGegevens, zaak));
        }
    }

    @GET
    @Path("zaak/{zaakUuid}")
    public List<RESTBAGObject> listBagobjectenVoorZaak(@PathParam("zaakUuid") final UUID zaakUUID) {
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        final Zaak zaak = zrcClientService.readZaak(zaakUUID);
        assertPolicy(policyService.readZaakRechten(zaak).getLezen());
        zaakobjectListParameters.setZaak(zaak.getUrl());
        final Results<Zaakobject> zaakobjecten = zrcClientService.listZaakobjecten(zaakobjectListParameters);
        if (zaakobjecten.getCount() > 0) {
            return zaakobjecten.getResults().stream()
                    .filter(Zaakobject::isBagObject)
                    .map(this::convertToBAGObject)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    private String getExpand(final BAGObjectType... bagObjectTypes) {
        return Arrays.stream(bagObjectTypes).map(BAGObjectType::getExpand).collect(Collectors.joining(","));
    }

    private RESTBAGObject convertToBAGObject(final Zaakobject zaakobject) {
        return switch (zaakobject.getObjectType()) {
            case ADRES -> adresConverter.convertToREST((ZaakobjectAdres) zaakobject);
            case PAND -> pandConverter.convertToREST((ZaakobjectPand) zaakobject);
            case WOONPLAATS -> woonplaatsConverter.convertToREST((ZaakobjectWoonplaats) zaakobject);
            case OPENBARE_RUIMTE -> openbareRuimteConverter.convertToREST((ZaakobjectOpenbareRuimte) zaakobject);
            case OVERIGE -> nummeraanduidingConverter.convertToREST((ZaakobjectNummeraanduiding) zaakobject); // voor nu alleen nummeraanduiding
            default -> throw new IllegalStateException("Unexpected objectType: " + zaakobject.getObjectType());
        };
    }

    private boolean isNogNietGekoppeld(final RESTBAGObject restbagObject, final Zaak zaak) {
        final ZaakobjectListParameters zaakobjectListParameters = new ZaakobjectListParameters();
        zaakobjectListParameters.setZaak(zaak.getUrl());
        zaakobjectListParameters.setObject(restbagObject.url);
        switch (restbagObject.getBagObjectType()) {
            case ADRES -> zaakobjectListParameters.setObjectType(Objecttype.ADRES);
            case ADRESSEERBAAR_OBJECT, NUMMERAANDUIDING -> zaakobjectListParameters.setObjectType(Objecttype.OVERIGE);
            case WOONPLAATS -> zaakobjectListParameters.setObjectType(Objecttype.WOONPLAATS);
            case PAND -> zaakobjectListParameters.setObjectType(Objecttype.PAND);
            case OPENBARE_RUIMTE -> zaakobjectListParameters.setObjectType(Objecttype.OPENBARE_RUIMTE);
        }
        final Results<Zaakobject> zaakobjecten = zrcClientService.listZaakobjecten(zaakobjectListParameters);
        return zaakobjecten.getResults().isEmpty();
    }
}
