/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.brc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParams;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.zgw.brc.model.Besluit;
import net.atos.client.zgw.brc.model.BesluitInformatieobject;
import net.atos.client.zgw.brc.model.BesluitenListParameters;
import net.atos.client.zgw.shared.exception.FoutExceptionMapper;
import net.atos.client.zgw.shared.exception.RuntimeExceptionMapper;
import net.atos.client.zgw.shared.exception.ValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.JsonbConfiguration;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;

/**
 * BRC Client
 */
@RegisterRestClient(configKey = "ZGW-API-Client")
@RegisterClientHeaders(ZGWClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(FoutExceptionMapper.class),
        @RegisterProvider(ValidatieFoutExceptionMapper.class),
        @RegisterProvider(RuntimeExceptionMapper.class),
        @RegisterProvider(JsonbConfiguration.class)})
@Path("besluiten/api/v1")
@Produces(APPLICATION_JSON)
public interface BRCClient {

    String ACCEPT_CRS = "Accept-Crs";

    String ACCEPT_CRS_VALUE = "EPSG:4326";

    String CONTENT_CRS = "Content-Crs";

    String CONTENT_CRS_VALUE = ACCEPT_CRS_VALUE;

    @GET
    @Path("besluiten")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Results<Besluit> besluitList(@BeanParam final BesluitenListParameters parameters);

    @POST
    @Path("besluiten")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Besluit besluitCreate(final Besluit besluit);

    @PUT
    @Path("besluiten/{uuid}")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Besluit besluitUpdate(@PathParam("uuid") final UUID uuid, final Besluit besluit);

    @GET
    @Path("besluiten/{besluit_uuid}/audittrail")
    List<AuditTrailRegel> listAuditTrail(@PathParam("besluit_uuid") final UUID besluitUUID);

    @GET
    @Path("besluiten/{uuid}")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Besluit besluitRead(@PathParam("uuid") final UUID uuid);

    @PATCH
    @Path("besluiten/{uuid}")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    Besluit besluitPartialUpdate(@PathParam("uuid") final UUID uuid, final Besluit besluit);

    @DELETE
    @Path("besluiten/{uuid}")
    Response besluitDelete(@PathParam("uuid") final UUID uuid);

    @GET
    @Path("besluitinformatieobjecten")
    List<BesluitInformatieobject> listBesluitInformatieobjecten(@QueryParam("besluit") final URI besluit);

    @POST
    @Path("besluitinformatieobjecten")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    BesluitInformatieobject besluitinformatieobjectCreate(final BesluitInformatieobject besluitInformatieobject);

}
