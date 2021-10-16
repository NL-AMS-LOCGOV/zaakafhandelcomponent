/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectData;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectListParameters;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectWithLockData;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.drc.model.LockEnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.ObjectInformatieObjectListParameters;
import net.atos.client.zgw.drc.model.Objectinformatieobject;
import net.atos.client.zgw.drc.model.UnlockEnkelvoudigInformatieObject;
import net.atos.client.zgw.shared.InvocationBuilderFactory;
import net.atos.client.zgw.shared.exception.FoutExceptionMapper;
import net.atos.client.zgw.shared.exception.RuntimeExceptionMapper;
import net.atos.client.zgw.shared.exception.ValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;

/**
 *
 */
@RegisterRestClient(configKey = "ZGW-API-Client")
@RegisterClientHeaders(ZGWClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(FoutExceptionMapper.class),
        @RegisterProvider(ValidatieFoutExceptionMapper.class),
        @RegisterProvider(RuntimeExceptionMapper.class)})
@Path("documenten/api/v1")
@Produces({APPLICATION_JSON, APPLICATION_PROBLEM_JSON})
public interface DRCClient {

    @POST
    @Path("enkelvoudiginformatieobjecten")
    EnkelvoudigInformatieObjectData enkelvoudiginformatieobjectCreate(final EnkelvoudigInformatieObjectData enkelvoudigInformatieObjectData);

    @GET
    @Path("enkelvoudiginformatieobjecten")
    Results<EnkelvoudigInformatieObject> enkelvoudiginformatieobjectList(@BeanParam final EnkelvoudigInformatieObjectListParameters parameters);

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieObject enkelvoudiginformatieobjectRead(@PathParam("uuid") final UUID uuid);

    @GET
    @Produces(APPLICATION_OCTET_STREAM)
    @Path("enkelvoudiginformatieobjecten/{uuid}/download")
    Response enkelvoudiginformatieobjectDownload(@PathParam("uuid") final UUID uuid, @QueryParam("versie") final Integer versie);


    @PATCH
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieObjectWithLockData enkelvoudiginformatieobjectPartialUpdate(@PathParam("uuid") final UUID uuid,
            final EnkelvoudigInformatieObjectWithLockData enkelvoudigInformatieObjectWithLockData);

    @DELETE
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    Response enkelvoudiginformatieobjectDelete(@PathParam("uuid") final UUID uuid);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/lock")
    LockEnkelvoudigInformatieObject enkelvoudiginformatieobjectLock(@PathParam("uuid") final UUID uuid,
            final LockEnkelvoudigInformatieObject lockEnkelvoudigInformatieObject);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/unlock")
    Response enkelvoudiginformatieobjectUnlock(@PathParam("uuid") final UUID uuid, final UnlockEnkelvoudigInformatieObject unlockEnkelvoudigInformatieObject);

    @POST
    @Path("gebruiksrechten")
    Gebruiksrechten gebruiksrechtenCreate(final Gebruiksrechten gebruiksrechten);

    @GET
    @Path("objectinformatieobjecten")
    Results<Objectinformatieobject> objectinformatieobjectList(@BeanParam final ObjectInformatieObjectListParameters parameters);

    static EnkelvoudigInformatieObject getEnkelvoudigInformatieObject(final URI uri) {
        return InvocationBuilderFactory.create(uri).get(EnkelvoudigInformatieObject.class);
    }
}
