/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.object;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.atos.client.or.shared.util.Constant.APPLICATION_PROBLEM_JSON;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParams;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.or.object.model.ORObject;
import net.atos.client.or.object.util.ObjectsClientHeadersFactory;
import net.atos.client.or.shared.exception.FoutExceptionMapper;
import net.atos.client.or.shared.exception.RuntimeExceptionMapper;
import net.atos.client.or.shared.exception.ValidatieFoutExceptionMapper;

/**
 *
 */
@RegisterRestClient(configKey = "Objects-API-Client")
@RegisterClientHeaders(ObjectsClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(FoutExceptionMapper.class),
        @RegisterProvider(ValidatieFoutExceptionMapper.class),
        @RegisterProvider(RuntimeExceptionMapper.class)})
@Produces({APPLICATION_JSON, APPLICATION_PROBLEM_JSON})
@Path("api/v1")
public interface ObjectsClient {

    String ACCEPT_CRS = "Accept-Crs";

    String ACCEPT_CRS_VALUE = "EPSG:4326";

    String CONTENT_CRS = "Content-Crs";

    String CONTENT_CRS_VALUE = ACCEPT_CRS_VALUE;

    @POST
    @Path("objects")
    @ClientHeaderParams({
            @ClientHeaderParam(name = ACCEPT_CRS, value = ACCEPT_CRS_VALUE),
            @ClientHeaderParam(name = CONTENT_CRS, value = CONTENT_CRS_VALUE)})
    ORObject objectCreate(final ORObject object);

    @GET
    @Path("objects/{object-uuid}")
    ORObject objectRead(@PathParam("object-uuid") final UUID objectUUID
    );
}
