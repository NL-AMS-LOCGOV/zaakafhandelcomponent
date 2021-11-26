/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobject;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectListParameters;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithInhoud;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectWithLockAndInhoud;
import net.atos.client.zgw.drc.model.Gebruiksrechten;
import net.atos.client.zgw.drc.model.Lock;
import net.atos.client.zgw.drc.model.ObjectInformatieobject;
import net.atos.client.zgw.drc.model.ObjectInformatieobjectListParameters;
import net.atos.client.zgw.shared.exception.FoutExceptionMapper;
import net.atos.client.zgw.shared.exception.RuntimeExceptionMapper;
import net.atos.client.zgw.shared.exception.ValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
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
    EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieobjectCreate(final EnkelvoudigInformatieobjectWithInhoud enkelvoudigInformatieObjectWithInhoud);

    @GET
    @Path("enkelvoudiginformatieobjecten")
    Results<EnkelvoudigInformatieobject> enkelvoudigInformatieobjectList(@BeanParam final EnkelvoudigInformatieobjectListParameters parameters);

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieobject enkelvoudigInformatieobjectRead(@PathParam("uuid") final UUID uuid);

    @GET
    @Produces(APPLICATION_OCTET_STREAM)
    @Path("enkelvoudiginformatieobjecten/{uuid}/download")
    Response enkelvoudigInformatieobjectDownload(@PathParam("uuid") final UUID uuid, @QueryParam("versie") final Integer versie);


    @PATCH
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieobjectWithLockAndInhoud enkelvoudigInformatieobjectPartialUpdate(@PathParam("uuid") final UUID uuid,
            final EnkelvoudigInformatieobjectWithLockAndInhoud enkelvoudigInformatieObjectWithLockAndInhoud);

    @DELETE
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    Response enkelvoudigInformatieobjectDelete(@PathParam("uuid") final UUID uuid);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/lock")
    Lock enkelvoudigInformatieobjectLock(@PathParam("uuid") final UUID uuid,
            final Lock enkelvoudigInformatieObjectLock);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/unlock")
    Response enkelvoudigInformatieobjectUnlock(@PathParam("uuid") final UUID uuid, final Lock lock);

    @POST
    @Path("gebruiksrechten")
    Gebruiksrechten gebruiksrechtenCreate(final Gebruiksrechten gebruiksrechten);

    @GET
    @Path("objectinformatieobjecten")
    Results<ObjectInformatieobject> objectInformatieobjectList(@BeanParam final ObjectInformatieobjectListParameters parameters);

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}/audittrail")
    List<AuditTrailRegel> listAuditTrail(@PathParam("uuid") UUID enkelvoudigInformatieobjectUUID);
}
