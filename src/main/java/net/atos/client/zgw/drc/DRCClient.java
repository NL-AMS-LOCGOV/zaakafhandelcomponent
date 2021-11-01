/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static net.atos.client.zgw.shared.util.Constants.APPLICATION_PROBLEM_JSON;

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

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieObjectListParameters;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectData;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithLockData;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.drc.model.LockEnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.ObjectInformatieobjectListParameters;
import net.atos.client.zgw.drc.model.ObjectInformatieobject;
import net.atos.client.zgw.drc.model.UnlockEnkelvoudigInformatieObject;
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
    EnkelvoudigInformatieobjectData enkelvoudigInformatieobjectCreate(final EnkelvoudigInformatieobjectData enkelvoudigInformatieObjectData);

    @GET
    @Path("enkelvoudiginformatieobjecten")
    Results<EnkelvoudigInformatieobject> enkelvoudigInformatieobjectList(@BeanParam final EnkelvoudigInformatieObjectListParameters parameters);

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieobject enkelvoudigInformatieobjectRead(@PathParam("uuid") final UUID uuid);

    @GET
    @Produces(APPLICATION_OCTET_STREAM)
    @Path("enkelvoudiginformatieobjecten/{uuid}/download")
    Response enkelvoudigInformatieobjectDownload(@PathParam("uuid") final UUID uuid, @QueryParam("versie") final Integer versie);


    @PATCH
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieobjectWithLockData enkelvoudigInformatieobjectPartialUpdate(@PathParam("uuid") final UUID uuid,
            final EnkelvoudigInformatieobjectWithLockData enkelvoudigInformatieObjectWithLockData);

    @DELETE
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    Response enkelvoudigInformatieobjectDelete(@PathParam("uuid") final UUID uuid);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/lock")
    LockEnkelvoudigInformatieObject enkelvoudigInformatieobjectLock(@PathParam("uuid") final UUID uuid,
            final LockEnkelvoudigInformatieObject lockEnkelvoudigInformatieObject);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/unlock")
    Response enkelvoudigInformatieobjectUnlock(@PathParam("uuid") final UUID uuid, final UnlockEnkelvoudigInformatieObject unlockEnkelvoudigInformatieObject);

    @POST
    @Path("gebruiksrechten")
    Gebruiksrechten gebruiksrechtenCreate(final Gebruiksrechten gebruiksrechten);

    @GET
    @Path("objectinformatieobjecten")
    Results<ObjectInformatieobject> objectInformatieobjectList(@BeanParam final ObjectInformatieobjectListParameters parameters);
}
