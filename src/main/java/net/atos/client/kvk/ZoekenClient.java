/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * API Zoeken
 * Documentatie voor API Zoeken.
 * <p>
 * The version of the OpenAPI document: 1.3
 */
package net.atos.client.kvk;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.kvk.exception.KvKClientNoResultExceptionMapper;
import net.atos.client.kvk.exception.RuntimeExceptionMapper;
import net.atos.client.kvk.model.KVKZoekenParameters;
import net.atos.client.kvk.zoeken.model.Resultaat;


@RegisterRestClient(configKey = "KVK-API-Client")
@RegisterProviders({
        @RegisterProvider(RuntimeExceptionMapper.class),
        @RegisterProvider(KvKClientNoResultExceptionMapper.class)
})
@Produces({"application/hal+json"})
@Path("api/v1/zoeken")
@Timeout(unit = ChronoUnit.SECONDS, value = 5)
public interface ZoekenClient {

    /**
     * Voor een bedrijf zoeken naar basisinformatie.
     * <p>
     * Er wordt max. 1000 resultaten getoond.
     */
    @GET
    Resultaat getResults(@BeanParam final KVKZoekenParameters zoekenParameters);

    /**
     * Voor een bedrijf zoeken naar basisinformatie asynchron.
     * <p>
     * Er wordt max. 1000 resultaten getoond.
     */
    @GET
    CompletionStage<Resultaat> getResultsAsync(@BeanParam final KVKZoekenParameters zoekenParameters);
}
