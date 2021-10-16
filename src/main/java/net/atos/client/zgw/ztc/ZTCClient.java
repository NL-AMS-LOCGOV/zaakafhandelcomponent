/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.ztc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.UUID;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.zgw.shared.exception.FoutExceptionMapper;
import net.atos.client.zgw.shared.exception.RuntimeExceptionMapper;
import net.atos.client.zgw.shared.exception.ValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.util.Constants;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;
import net.atos.client.zgw.ztc.model.Catalogus;
import net.atos.client.zgw.ztc.model.CatalogusListParameters;
import net.atos.client.zgw.ztc.model.Eigenschap;
import net.atos.client.zgw.ztc.model.EigenschapListParameters;
import net.atos.client.zgw.ztc.model.Informatieobjecttype;
import net.atos.client.zgw.ztc.model.Resultaattype;
import net.atos.client.zgw.ztc.model.ResultaattypeListParameters;
import net.atos.client.zgw.ztc.model.Roltype;
import net.atos.client.zgw.ztc.model.RoltypeListParameters;
import net.atos.client.zgw.ztc.model.Statustype;
import net.atos.client.zgw.ztc.model.StatustypeListParameters;
import net.atos.client.zgw.ztc.model.Zaaktype;
import net.atos.client.zgw.ztc.model.ZaaktypeListParameters;

/**
 * Let op! Methods met caching vind je in ZTCClientService, deze methods NIET direct gebruiken!!!
 */
@RegisterRestClient(configKey = "ZGW-API-Client")
@RegisterClientHeaders(ZGWClientHeadersFactory.class)
@RegisterProviders({
        @RegisterProvider(FoutExceptionMapper.class),
        @RegisterProvider(ValidatieFoutExceptionMapper.class),
        @RegisterProvider(RuntimeExceptionMapper.class)})
@Path("catalogi/api/v1")
@Produces({APPLICATION_JSON, Constants.APPLICATION_PROBLEM_JSON})
public interface ZTCClient {

    @GET
    @Path("catalogussen")
    Results<Catalogus> catalogusList(@BeanParam final CatalogusListParameters parameters);

    @GET
    @Path("eigenschappen")
    Results<Eigenschap> eigenschapList(@BeanParam final EigenschapListParameters parameters);

    @GET
    @Path("informatieobjecttypen")
    Results<Informatieobjecttype> informatieobjecttypeList();

    @GET
    @Path("resultaattypen")
    Results<Resultaattype> resultaattypeList(@BeanParam final ResultaattypeListParameters parameters);

    @GET
    @Path("roltypen")
    Results<Roltype> roltypeList(@BeanParam final RoltypeListParameters parameters);

    @GET
    @Path("statustypen")
    Results<Statustype> statustypeList(@BeanParam final StatustypeListParameters parameters);

    @GET
    @Path("statustypen/{uuid}")
    Statustype statustypeRead(@PathParam("uuid") final UUID uuid);

    @GET
    @Path("zaaktypen")
    Results<Zaaktype> zaaktypeList(@BeanParam final ZaaktypeListParameters parameters);

    @GET
    @Path("zaaktypen/{uuid}")
    Zaaktype zaaktypeRead(@PathParam("uuid") final UUID uuid);
}
