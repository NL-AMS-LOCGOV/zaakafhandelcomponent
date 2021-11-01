/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.zrc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParams;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.zgw.shared.exception.FoutExceptionMapper;
import net.atos.client.zgw.shared.exception.RuntimeExceptionMapper;
import net.atos.client.zgw.shared.exception.ValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;
import net.atos.client.zgw.zrc.model.Resultaat;
import net.atos.client.zgw.zrc.model.Rol;
import net.atos.client.zgw.zrc.model.RolListParameters;
import net.atos.client.zgw.zrc.model.Status;
import net.atos.client.zgw.zrc.model.Zaak;
import net.atos.client.zgw.zrc.model.ZaakInformatieObject;
import net.atos.client.zgw.zrc.model.ZaakListParameters;
import net.atos.client.zgw.zrc.model.Zaakeigenschap;
import net.atos.client.zgw.zrc.model.ZaakinformatieobjectListParameters;
import net.atos.client.zgw.zrc.model.Zaakobject;
import net.atos.client.zgw.zrc.model.ZaakobjectListParameters;

/**
 *
 */
@RegisterRestClient(configKey = "ZGW-API-Client")
@RegisterClientHeaders(ZGWClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(FoutExceptionMapper.class),
        @RegisterProvider(ValidatieFoutExceptionMapper.class),
        @RegisterProvider(RuntimeExceptionMapper.class)})
@Path("zaken/api/v1")
@Produces({APPLICATION_JSON, APPLICATION_PROBLEM_JSON})
public interface ZRCClient {

    String ACCEPT_CRS = "Accept-Crs";

    String ACCEPT_CRS_VALUE = "EPSG:4326";

    String CONTENT_CRS = "Content-Crs";

    String CONTENT_CRS_VALUE = ACCEPT_CRS_VALUE;

    @GET
    @Path("zaken")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Results<Zaak> zaakList(@BeanParam final ZaakListParameters parameters);


    @POST
    @Path("zaken")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Zaak zaakCreate(final Zaak zaak);

    @PATCH
    @Path("zaken/{uuid}")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Zaak zaakPartialUpdate(@PathParam("uuid") final UUID uuid, final Zaak zaak);

    @GET
    @Path("zaken/{uuid}")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Zaak zaakRead(@PathParam("uuid") final UUID uuid);

    @GET
    @Path("rollen")
    Results<Rol<?>> rolList(@BeanParam final RolListParameters parameters);

    @POST
    @Path("rollen")
    Rol<?> rolCreate(final Rol<?> rol);

    @DELETE
    @Path("rollen/{uuid}")
    Response rolDelete(@PathParam("uuid") final UUID uuid);

    @GET
    @Path("zaakinformatieobjecten")
    List<ZaakInformatieObject> zaakinformatieobjectList(@BeanParam final ZaakinformatieobjectListParameters parameters);

    @POST
    @Path("zaakinformatieobjecten")
    ZaakInformatieObject zaakinformatieobjectCreate(final ZaakInformatieObject zaakInformatieObject);

    @DELETE
    @Path("zaakinformatieobjecten/{uuid}")
    Response zaakinformatieobjectDelete(@PathParam("uuid") final UUID uuid);

    @POST
    @Path("statussen")
    Status statusCreate(final Status status);

    @POST
    @Path("resultaten")
    Resultaat resultaatCreate(final Resultaat resultaat);

    @GET
    @Path("zaken/{zaak_uuid}/zaakeigenschappen")
    List<Zaakeigenschap> zaakeigenschapList(@PathParam("zaak_uuid") final UUID zaakUUID);

    @POST
    @Path("zaken/{zaak_uuid}/zaakeigenschappen")
    Zaakeigenschap zaakeigenschapCreate(@PathParam("zaak_uuid") final UUID zaakUUID, final Zaakeigenschap zaakeigenschap);

    @GET
    @Path("zaakobjecten")
    Results<Zaakobject> zaakobjectList(@BeanParam final ZaakobjectListParameters zaakobjectListParameters);

    @POST
    @Path("zaakobjecten")
    Zaakobject zaakobjectCreate(final Zaakobject zaakobject);
}
