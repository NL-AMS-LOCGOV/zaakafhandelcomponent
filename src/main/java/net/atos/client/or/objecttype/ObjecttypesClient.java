/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.objecttype;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.or.objecttype.model.Objecttype;
import net.atos.client.or.objecttype.model.ObjecttypeVersion;
import net.atos.client.or.shared.exception.FoutExceptionMapper;
import net.atos.client.or.shared.exception.RuntimeExceptionMapper;
import net.atos.client.or.shared.exception.ValidatieFoutExceptionMapper;

/**
 *
 */
@RegisterRestClient(configKey = "Objecttypes-API-Client")
@RegisterClientHeaders(ObjecttypesClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(FoutExceptionMapper.class),
        @RegisterProvider(ValidatieFoutExceptionMapper.class),
        @RegisterProvider(RuntimeExceptionMapper.class)
})
@Produces(APPLICATION_JSON)
@Path("api/v2")
public interface ObjecttypesClient {

    @GET
    @Path("objecttypes")
    List<Objecttype> objecttypeList();

    @GET
    @Path("objecttypes/{objecttype-uuid}")
    Objecttype objecttypeRead(@PathParam("objecttype-uuid") final UUID objecttypeUUID);

    @GET
    @Path("objecttypes/{objecttype-uuid}/versions")
    List<ObjecttypeVersion> objectversionList(@PathParam("objecttype-uuid") final UUID objecttypeUUID);

    @GET
    @Path("objecttypes/{objecttype-uuid}/versions/{version}")
    ObjecttypeVersion objectversionRead(@PathParam("objecttype-uuid") final UUID objecttypeUUID, @PathParam("version") final Integer version);
}
